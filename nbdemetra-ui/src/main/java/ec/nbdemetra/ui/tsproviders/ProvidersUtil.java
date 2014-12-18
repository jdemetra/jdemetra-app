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
import ec.nbdemetra.ui.Config;
import ec.tss.tsproviders.DataSource;
import javax.annotation.Nonnull;
import org.openide.nodes.Node;

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
}
