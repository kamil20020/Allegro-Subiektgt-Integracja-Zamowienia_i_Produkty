package pl.kamil_dywan.gui;

import pl.kamil_dywan.exception.UnloggedException;
import pl.kamil_dywan.external.allegro.generated.Payment;
import pl.kamil_dywan.external.allegro.generated.buyer.Buyer;
import pl.kamil_dywan.external.allegro.generated.invoice_item.LineItem;
import pl.kamil_dywan.external.allegro.generated.order.Order;
import pl.kamil_dywan.external.allegro.generated.order.OrderResponse;
import pl.kamil_dywan.external.allegro.generated.order.Summary;
import pl.kamil_dywan.service.OrderService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Locale;

public class OrdersGui implements ChangeableGui {

    private JPanel mainPanel;

    private JPanel ordersPanelPlaceholder;
    private PaginationTableGui paginationTableGui;

    private JButton saveOrdersButton;

    private List<Order> orders;

    private final OrderService orderService;
    private final Runnable handleLogout;

    public OrdersGui(OrderService orderService, Runnable handleLogout) {

        this.orderService = orderService;
        this.handleLogout = handleLogout;

        $$$setupUI$$$();

        saveOrdersButton.addActionListener(e -> saveOrdersToFile());
    }

    private PaginationTableGui.PaginationTableData loadData(int offset, int limit) {

        OrderResponse orderResponse;

        try {
            orderResponse = orderService.getPage(offset, limit);
        } catch (UnloggedException e) {

            handleLogout.run();

            return null;
        }

        orders = orderResponse.getOrders();

        int totalNumberOfRows = orderResponse.getTotalCount();

        PaginationTableGui.PaginationTableData data = new PaginationTableGui.PaginationTableData(
                orders,
                totalNumberOfRows
        );

        return data;
    }

    private String[] convertToRow(Object rawOrder) {

        Order order = (Order) rawOrder;
        Buyer orderBuyer = order.getBuyer();
        List<LineItem> orderLineItems = order.getLineItems();
        Summary orderSummary = order.getSummary();
        Payment orderPayment = order.getPayment();

        return new String[]{
                order.getId().toString(),
                orderBuyer.getFirstName() + " " + orderBuyer.getLastName(),
                String.valueOf(orderLineItems.size()),
                orderSummary.getTotalToPay().getAmount().toString() + " zł",
                orderPayment.getFinishedAt().toLocalDate().toString()
        };
    }

    private void saveOrdersToFile() {

        FileDialog fileDialog = new FileDialog((Frame) null, "Zapisywanie zamówień do pliku", FileDialog.SAVE);

        File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();

        fileDialog.setDirectory(homeDirectory.getAbsolutePath());
        fileDialog.setFile("zamowienia.xml");
        fileDialog.setFilenameFilter((directory, name) -> name.endsWith(".xml"));

        fileDialog.setVisible(true);

        String savedFileName = fileDialog.getFile();

        if (savedFileName == null) {
            return;
        }

        String savedFilePath = fileDialog.getDirectory() + savedFileName;

        try {
            orderService.writeOrdersToFile(orders, savedFilePath);
        } catch (IllegalStateException e) {

            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Nie udało się zapisać zamówień do pliku",
                    "Powiadomienie o błędzie",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        JOptionPane.showMessageDialog(
                mainPanel,
                "Zapisano zamówienia do pliku " + savedFilePath,
                "Powiadomienie",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void load() {

        paginationTableGui.handleLoadTableExceptions();
    }

    public JPanel getMainPanel() {

        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        String[] tableHeaders = {"Identyfikator", "Kupujący", "Liczba ofert", "Kwota brutto", "Data"};
        Integer[] tableWidths = {100, 100, 100, 100, 100};

        paginationTableGui = new PaginationTableGui(tableHeaders, tableWidths, this::loadData, this::convertToRow);

        ordersPanelPlaceholder = paginationTableGui.getMainPanel();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setAlignmentX(0.0f);
        mainPanel.setAlignmentY(0.0f);
        mainPanel.setAutoscrolls(false);
        mainPanel.setMinimumSize(new Dimension(478, 138));
        mainPanel.setOpaque(true);
        mainPanel.setPreferredSize(new Dimension(1920, 980));
        mainPanel.setRequestFocusEnabled(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(16, 50, 40, 50), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setAlignmentX(0.0f);
        Font label1Font = this.$$$getFont$$$(null, -1, 26, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setHorizontalAlignment(0);
        label1.setHorizontalTextPosition(0);
        label1.setText("Zamówienia");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(label1, gbc);
        ordersPanelPlaceholder.setOpaque(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 10.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 0, 0);
        mainPanel.add(ordersPanelPlaceholder, gbc);
        ordersPanelPlaceholder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        saveOrdersButton = new JButton();
        saveOrdersButton.setAlignmentX(0.5f);
        saveOrdersButton.setOpaque(true);
        saveOrdersButton.setText("Zapisz zamówienia do pliku");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(saveOrdersButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
