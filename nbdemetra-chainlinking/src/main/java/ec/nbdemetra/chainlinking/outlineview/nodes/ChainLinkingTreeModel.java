/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package ec.nbdemetra.chainlinking.outlineview.nodes;

import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataBlock;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.YearIterator;
import ec.tstoolkit.timeseries.simplets.chainlinking.AChainLinking.Product;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Model used for the tree table displaying added products with their annual and
 * quarterly values
 *
 * @author Mats Maggi
 */
public class ChainLinkingTreeModel implements TreeModel {

    private final RootNode root;

    public ChainLinkingTreeModel(List<Product> products) {
        this.root = new RootNode(products);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return getChildren(parent)[index];
    }

    protected Object[] getChildren(Object node) {
        return ((CustomNode) node).getChildren();
    }

    @Override
    public int getChildCount(Object parent) {
        Object[] children = getChildren(parent);
        return (children == null) ? 0 : children.length;
    }

    @Override
    public boolean isLeaf(Object node) {
        if (node instanceof CustomNode) {
            return ((CustomNode) node).isLeaf();
        }
        return false;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        CustomNode par = (CustomNode) parent;
        CustomNode ch = (CustomNode) child;
        return Arrays.asList(par.getChildren()).indexOf(ch);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {

    }

    public interface CustomNode {

        public Object[] getChildren();

        public boolean isLeaf();
    }

    /**
     * Node representing the root element of the tree. This node is not
     * displayed in the UI
     */
    public static class RootNode implements CustomNode {

        private final List<Product> products;
        private Object[] children;

        public RootNode(List<Product> products) {
            this.products = products;
        }

        @Override
        public String toString() {
            return "Products";
        }

        @Override
        public Object[] getChildren() {
            if (products == null || products.isEmpty()) {
                return null;
            }

            if (children == null) {
                int nbElem = products.size();
                children = new Object[nbElem];
                for (int i = 0; i < nbElem; i++) {
                    children[i] = new ProductNode(products.get(i));
                }
            }
            return children;
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

    }

    /**
     * Node representing a product. Its children are the annually aggregated
     * values of the quantities and price. These information are included in a
     * {@link YearlyNode}
     */
    public static class ProductNode implements CustomNode {

        private final Product product;
        private Object[] children;

        public ProductNode(Product product) {
            this.product = product;
        }

        public String getName() {
            return product.getName();
        }

        public Product getProduct() {
            return product;
        }

        @Override
        public Object[] getChildren() {
            if (children == null) {
                int nbElem = product.getQuantities().changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true).getLength();
                children = new Object[nbElem];
                TsData quantQuarterly = product.getQuantities().changeFrequency(TsFrequency.Quarterly, TsAggregationType.Sum, true);
                TsData priceQuarterly = product.getPrice().changeFrequency(TsFrequency.Quarterly, TsAggregationType.Sum, true);

                quantQuarterly = quantQuarterly.fullYears();
                priceQuarterly = priceQuarterly.fullYears();

                YearIterator quantIt = new YearIterator(quantQuarterly);
                YearIterator priceIt = new YearIterator(priceQuarterly);
                int i = 0;

                while (quantIt.hasMoreElements()) {
                    TsDataBlock quantities = quantIt.nextElement();
                    TsDataBlock price = priceIt.nextElement();
                    Object[] quarterlyNodes = new Object[quantities.data.getLength()];
                    for (int j = 0; j < quantities.data.getLength(); j++) {
                        Double q = quantities.data.get(j);
                        Double p = price.data.get(j);
                        String period = quantities.period(j).getPeriodString();
                        quarterlyNodes[j] = new QuarterlyNode(period, q, p, null);
                    }

                    YearlyNode yn = new YearlyNode(quantities.start.getYear(), quantities.data.sum(), price.data.sum() / 4.0, null);
                    yn.setChildren(quarterlyNodes);
                    children[i++] = yn;
                }

            }
            return children;
        }

        @Override
        public String toString() {
            return product.getName();
        }

        @Override
        public boolean isLeaf() {
            return false;
        }
    }

    /**
     * Node representing yearly aggregated values of a product for a given year.
     * Its children are the quarterly values of the corresponding year. These
     * information are contained in a {@link QuarterlyNode}.
     */
    public static class YearlyNode implements CustomNode {

        private Object[] children;
        private final int year;
        private Double q, p, v;

        public YearlyNode(int year, Double q, Double p, Double v) {
            this.year = year;
            this.q = q;
            this.p = p;
            this.v = v;
        }

        public Double getQ() {
            return q;
        }

        public Double getP() {
            return p;
        }

        public Double getV() {
            return v;
        }

        @Override
        public String toString() {
            return String.valueOf(year);
        }

        public void setChildren(Object[] nodes) {
            children = nodes;
        }

        @Override
        public Object[] getChildren() {
            return children;
        }

        @Override
        public boolean isLeaf() {
            return false;
        }
    }

    /**
     * Node representing quarterly values of a product for a given quarter.
     */
    public static class QuarterlyNode implements CustomNode {

        private final String title;
        private final Double q, p, v;

        public QuarterlyNode(String title, Double q, Double p, Double v) {
            this.title = title;
            this.q = q;
            this.p = p;
            this.v = v;
        }

        public Double getQ() {
            return q;
        }

        public Double getP() {
            return p;
        }

        public Double getV() {
            return v;
        }

        @Override
        public String toString() {
            return title;
        }

        @Override
        public Object[] getChildren() {
            return new Object[0];
        }

        @Override
        public boolean isLeaf() {
            return true;
        }
    }
}
