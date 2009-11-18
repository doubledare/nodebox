package nodebox.client;

import nodebox.Icons;
import nodebox.node.Node;
import nodebox.node.Parameter;
import nodebox.node.Port;
import nodebox.node.Port.Cardinality;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

public class NodeAttributesEditor extends JPanel implements ListSelectionListener {

    private Node node;

    private ParameterListModel parameterListModel;
    private Parameter selectedParameter = null;
    private Port selectedPort = null;
    private ParameterList parameterList;
    private JPanel editorPanel;

    private JButton removeButton;
    private JButton addButton;

    public NodeAttributesEditor(Node node) {
        setLayout(new BorderLayout(0, 0));
        //library = new CoreNodeTypeLibrary("test", new Version(1, 0, 0));
        this.node = node;
        parameterListModel = new ParameterListModel(node);
        ParameterCellRenderer parameterCellRenderer = new ParameterCellRenderer();
        parameterList = new ParameterList();
        reloadParameterList();
        //parameterList.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
        addButton = new JButton(new Icons.PlusIcon());
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEvent();
            }
        });
        removeButton = new JButton(new Icons.MinusIcon());
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeEvent();
            }
        });

        JButton upButton = new JButton(new Icons.ArrowIcon(Icons.ArrowIcon.NORTH));
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveUp();
            }
        });
        JButton downButton = new JButton(new Icons.ArrowIcon(Icons.ArrowIcon.SOUTH));
        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveDown();
            }
        });
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(upButton);
        buttonPanel.add(downButton);

        //parameterList.getSelectionModel().addListSelectionListener(this);
        //parameterList.setCellRenderer(parameterCellRenderer);
        //parameterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel leftPanel = new JPanel(new BorderLayout(5, 0));
        leftPanel.add(parameterList, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        SingleLineSplitter split = new SingleLineSplitter(NSplitter.Orientation.HORIZONTAL, leftPanel, editorPanel);
        split.setPosition(0.25f);
        split.setEnabled(false);
        add(split, BorderLayout.CENTER);
        if (node.getParameterCount() > 0)
            parameterList.setSelectedIndex(0);
    }

    private void reloadParameterList() {
        parameterList.removeAll();
        // Add the Node metadata.

        parameterList.addHeader("NODE");
        parameterList.addHeader("PORTS");
        for (Port p : node.getPorts()) {
            parameterList.addPort(p);
        }
        parameterList.addHeader("PARAMETERS");
        for (Parameter p : node.getParameters()) {
            parameterList.addParameter(p);
        }
        revalidate();
    }

    private void portSelected(Port p) {
        editorPanel.removeAll();
        PortAttributesEditor editor = new PortAttributesEditor(p);
        editorPanel.add(editor, BorderLayout.CENTER);
        editorPanel.revalidate();
        selectedPort = p;
        selectedParameter = null;
    }

    private void parameterSelected(Parameter p) {
        editorPanel.removeAll();
        ParameterAttributesEditor editor = new ParameterAttributesEditor(p);
        editorPanel.add(editor, BorderLayout.CENTER);
        editorPanel.revalidate();
        selectedParameter = p;
        selectedPort = null;
    }

    private void addEvent() {
        JMenuItem item;
        JPopupMenu menu = new JPopupMenu();
        item = menu.add("Parameter");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addParameter();
            }
        });
        item = menu.add("Port");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPort();
            }
        });
        menu.show(addButton, 0, addButton.getHeight());
    }

    private void addParameter() {
        String parameterName = JOptionPane.showInputDialog("Enter parameter name");
        if (parameterName != null) {
            Parameter parameter = node.addParameter(parameterName, Parameter.Type.FLOAT);
            reloadParameterList();
            parameterList.setSelectedValue(parameter, true);
        }
    }

    private void addPort() {
        JOptionPane.showMessageDialog(this, "Sorry, adding ports is not implemented yet.");
    }

    private void removeEvent() {
        if (selectedParameter != null) {
            removeSelectedParameter();
        } else if (selectedPort != null) {
            removeSelectedPort();
        }
    }

    private void removeSelectedPort() {
        node.removePort(selectedPort.getName());
        reloadParameterList();
    }

    private void removeSelectedParameter() {
        if (selectedParameter == null) return;
        boolean success = node.removeParameter(selectedParameter.getName());
        System.out.println("success = " + success);
        reloadParameterList();
        if (node.getParameterCount() > 0) {
            parameterList.setSelectedIndex(0);
        } else {
            parameterList.setSelectedValue(null, false);
        }
    }

    private void moveDown() {
        if (selectedParameter == null) return;
        java.util.List<Parameter> parameters = node.getParameters();
        int index = parameters.indexOf(selectedParameter);
        assert (index >= 0);
        if (index >= parameters.size() - 1) return;
        parameters.remove(selectedParameter);
        parameters.add(index + 1, selectedParameter);
        reloadParameterList();
        parameterList.setSelectedIndex(index + 1);
    }

    private void moveUp() {
        if (selectedParameter == null) return;
        java.util.List<Parameter> parameters = node.getParameters();
        int index = parameters.indexOf(selectedParameter);
        assert (index >= 0);
        if (index == 0) return;
        parameters.remove(selectedParameter);
        parameters.add(index - 1, selectedParameter);
        reloadParameterList();
        parameterList.setSelectedIndex(index - 1);
    }

    public Node getNode() {
        return node;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (selectedParameter == parameterList.getSelectedValue()) return;
        selectedParameter = (Parameter) parameterList.getSelectedValue();
        if (selectedParameter == null) {
            removeButton.setEnabled(false);
        } else {
            removeButton.setEnabled(true);
        }
        //parameterPanel.revalidate();
    }

    private class ParameterListModel implements ListModel {
        private java.util.List<Parameter> parameters;

        public ParameterListModel(Node node) {
            parameters = node.getParameters();
        }

        public int getSize() {
            return parameters.size();
        }

        public Object getElementAt(int index) {
            return parameters.get(index);
        }

        public void addListDataListener(ListDataListener l) {
            // Not implemented
        }

        public void removeListDataListener(ListDataListener l) {
            // Not implemented
        }
    }

    private class ParameterCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Parameter parameter = (Parameter) value;
            String displayValue = parameter.getLabel() + " (" + parameter.getName() + ")";
            return super.getListCellRendererComponent(list, displayValue, index, isSelected, cellHasFocus);
        }
    }

    private class SourceLabel extends JComponent {

        private String text;
        private Object source;
        private boolean selected;

        private SourceLabel(String text, Object source) {
            this.text = text;
            this.source = source;
            setMinimumSize(new Dimension(100, 25));
            setMaximumSize(new Dimension(500, 25));
            setPreferredSize(new Dimension(140, 25));
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            if (selected) {
                Rectangle clip = g2.getClipBounds();
                g2.setColor(Theme.NODE_ATTRIBUTES_PARAMETER_COLOR);
                g2.fillRect(clip.x, clip.y, clip.width, clip.height);
            }
            g2.setFont(Theme.SMALL_FONT);
            if (selected) {
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(Color.BLACK);
            }
            g2.drawString(text, 15, 18);
        }
    }

    private class ParameterList extends JPanel {

        private SourceLabel selectedLabel;
        private Map<Object, SourceLabel> labelMap = new HashMap<Object, SourceLabel>();

        private ParameterList() {
            super(null);
            Dimension d = new Dimension(140, 500);
            setBackground(Theme.NODE_ATTRIBUTES_PARAMETER_LIST_BACGKGROUND_COLOR);
            setBorder(null);
            setOpaque(true);
            setPreferredSize(d);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        public void addPort(final Port p) {
            final SourceLabel label = new SourceLabel(p.getName(), p);
            label.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    setSelectedLabel(label);
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }
            });
            labelMap.put(p, label);
            add(label);
        }

        public void addParameter(final Parameter p) {
            final SourceLabel label = new SourceLabel(p.getName(), p);
            label.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    setSelectedLabel(label);
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }
            });
            labelMap.put(p, label);
            add(label);
        }

        /**
         * Add a header label that cannot be selected.
         *
         * @param s the name of the header.
         */
        public void addHeader(String s) {
            JLabel header = new JLabel(s);
            header.setEnabled(false);
            header.setForeground(Theme.TEXT_DISABLED_COLOR);
            header.setFont(Theme.SMALL_BOLD_FONT);
            header.setMinimumSize(new Dimension(100, 25));
            header.setMaximumSize(new Dimension(500, 25));
            header.setPreferredSize(new Dimension(140, 25));
            add(header);
        }

        public void setSelectedLabel(SourceLabel label) {
            if (selectedLabel != null)
                selectedLabel.setSelected(false);
            selectedLabel = label;
            if (selectedLabel != null) {
                selectedLabel.setSelected(true);
                if (label.source instanceof Parameter)
                    parameterSelected((Parameter) label.source);
                else if (label.source instanceof Port)
                    portSelected((Port) label.source);
                else
                    throw new AssertionError("Unknown label source " + label.source);
            }
        }

        public void setSelectedIndex(int i) {
            // TODO: Implement
        }

        public void setSelectedValue(Object value, boolean shouldScroll) {
            SourceLabel label = labelMap.get(value);
            assert label != null;
            setSelectedLabel(label);
        }

        public Object getSelectedValue() {
            return null;
        }
    }

    public static void main(String[] args) {
        JFrame editorFrame = new JFrame();
        Node node = new NodeBoxDocument.AllControlsType().createInstance();
        node.addPort("shape");
        editorFrame.getContentPane().add(new NodeAttributesEditor(node));
        editorFrame.setSize(580, 710);
        editorFrame.setResizable(false);
        editorFrame.setLocationByPlatform(true);
        editorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        editorFrame.setVisible(true);
    }
}
