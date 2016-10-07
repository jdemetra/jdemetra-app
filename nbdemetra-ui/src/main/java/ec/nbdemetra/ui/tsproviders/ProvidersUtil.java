/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.ui.tsproviders;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.properties.ForwardingNodeProperty;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tss.TsAsyncMode;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceLoader;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.TsProviders;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.openide.ErrorManager;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
final class ProvidersUtil {

    private ProvidersUtil() {
        // static class
    }

    @Nonnull
    public static String getDataSourceDomain() {
        return DataSource.class.getName();
    }

    @Nonnull
    public static DataSource getDataSource(@Nonnull Config config) throws IllegalArgumentException {
        String uri = config.get("uri");
        if (uri == null) {
            throw new IllegalArgumentException("Missing parameter");
        }
        DataSource result = DataSource.uriParser().parse(uri);
        if (result == null) {
            throw new IllegalArgumentException("Invalid uri");
        }
        return result;
    }

    public static Config getConfig(DataSource dataSource, String displayName) {
        return Config.builder(getDataSourceDomain(), displayName, "")
                .put("uri", DataSource.uriFormatter().formatAsString(dataSource))
                .build();
    }

    public static Optional<Node> findNode(DataSource dataSource, Node node) {
        if (node instanceof ProvidersNode) {
            return find(dataSource, (ProvidersNode) node);
        }
        if (node instanceof ProviderNode) {
            return find(dataSource, (ProviderNode) node);
        }
        return Optional.absent();
    }

    private static Optional<Node> find(DataSource dataSource, ProvidersNode node) {
        for (Node o : node.getChildren().getNodes()) {
            if (dataSource.getProviderName().equals(o.getName())) {
                return find(dataSource, (ProviderNode) o);
            }
        }
        return Optional.absent();
    }

    private static Optional<Node> find(DataSource dataSource, ProviderNode node) {
        for (Node o : node.getChildren().getNodes()) {
            if (dataSource.equals(o.getLookup().lookup(DataSource.class))) {
                return Optional.of(o);
            }
        }
        return Optional.absent();
    }

    @Nonnull
    static Sheet sheetOf(List<Sheet.Set> sets) {
        Sheet result = new Sheet();
        sets.forEach(result::put);
        return result;
    }

    @Nonnull
    static List<Sheet.Set> sheetSetsOfProvider(@Nonnull String providerName) {
        Optional<IDataSourceProvider> op = TsProviders.lookup(IDataSourceProvider.class, providerName);
        if (op.isPresent()) {
            IDataSourceProvider provider = op.get();
            List<Sheet.Set> result = new ArrayList<>();
            NodePropertySetBuilder b = new NodePropertySetBuilder();
            b.with(String.class).select(provider, "getSource", null).display("Source").add();
            b.withEnum(TsAsyncMode.class).select(provider, "getAsyncMode", null).display("Async mode").add();
            b.with(Boolean.class).select(provider, "isAvailable", null).display("Available").add();
            b.withBoolean().selectConst("Loadable", provider instanceof IDataSourceLoader).add();
            b.withBoolean().selectConst("Files as source", provider instanceof IFileLoader).add();
            result.add(b.build());
            return result;
        }
        return Collections.emptyList();
    }

    @Nonnull
    static List<Sheet.Set> sheetSetsOfBean(@Nonnull Object bean) throws IntrospectionException {
        List<Sheet.Set> result = new ArrayList<>();
        for (Node.PropertySet o : new BeanNode<>(bean).getPropertySets()) {
            Sheet.Set set = Sheet.createPropertiesSet();
            set.put(o.getProperties());
            result.add(set);
        }
        return result;
    }

    @Nonnull
    static List<Sheet.Set> sheetSetsOfException(@Nonnull IOException ex) {
        List<Sheet.Set> result = new ArrayList<>();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("IOException");

        int i = 0;
        Throwable current = ex;
        while (current != null) {
            b.reset("throwable" + i++).display(current.getClass().getSimpleName());
            b.with(String.class).selectConst("Type", current.getClass().getName()).add();
            b.with(String.class).selectConst("Message", current.getMessage()).add();
            result.add(b.build());
            current = current.getCause();
        }

        return result;
    }

    @Nonnull
    static List<Sheet.Set> sheetSetsOfDataSource(@Nonnull DataSource dataSource) {
        return sheetSetsOfDataSource(dataSource, usingErrorManager(ProvidersUtil::sheetSetsOfBean, Collections::emptyList));
    }

    @Nonnull
    static List<Sheet.Set> sheetSetsOfDataSource(@Nonnull DataSource dataSource, @Nonnull Function<Object, List<Sheet.Set>> beanFunc) {
        List<Sheet.Set> result = new ArrayList<>();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("DataSource");
        b.with(String.class).select(dataSource, "getProviderName", null).display("Source").add();
        b.with(String.class).select(dataSource, "getVersion", null).display("Version").add();
        Optional<IDataSourceLoader> loader = TsProviders.lookup(IDataSourceLoader.class, dataSource);
        if (loader.isPresent()) {
            Object bean = loader.get().decodeBean(dataSource);
            beanFunc.apply(bean).stream()
                    .flatMap(set -> Stream.of(set.getProperties()))
                    .map(ForwardingNodeProperty::readOnly)
                    .forEach(b::add);
        }
        result.add(b.build());
        return result;
    }

    @Nonnull
    static List<Sheet.Set> sheetSetsOfDataSet(DataSet dataSet) {
        return sheetSetsOfDataSet(dataSet, ProvidersUtil::sheetSetsOfDataSource, ProvidersUtil::fillParamProperties);
    }

    @Nonnull
    static void fillParamProperties(@Nonnull NodePropertySetBuilder b, @Nonnull DataSet dataSet) {
        dataSet.getParams().forEach((k, v) -> {
            b.with(String.class).selectConst(k, v).add();
        });
    }

    @Nonnull
    static List<Sheet.Set> sheetSetsOfDataSet(@Nonnull DataSet dataSet,
            @Nonnull Function<DataSource, List<Sheet.Set>> sourceFunc,
            @Nonnull BiConsumer<NodePropertySetBuilder, DataSet> paramFiller) {
        List<Sheet.Set> result = Lists.newArrayList(sourceFunc.apply(dataSet.getDataSource()));
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("DataSet");
        b.withEnum(DataSet.Kind.class).select(dataSet, "getKind", null).display("Kind").add();
        paramFiller.accept(b, dataSet);
        result.add(b.build());
        return result;
    }

    @FunctionalInterface
    interface IntrospectionFunc<X, Y> {

        Y apply(X x) throws IntrospectionException;
    }

    @Nonnull
    static <X, Y> Function<X, Y> usingErrorManager(IntrospectionFunc<X, Y> func, Supplier<Y> defaultValue) {
        return (X x) -> {
            try {
                return func.apply(x);
            } catch (IntrospectionException ex) {
                ErrorManager.getDefault().log(ex.getMessage());
                return defaultValue.get();
            }
        };
    }
}
