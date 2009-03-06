package org.swtchart.ext.internal.properties;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IAxis;
import org.swtchart.LineStyle;
import org.swtchart.IAxis.Direction;

/**
 * The grid page on properties dialog.
 */
public class GridPage extends AbstractSelectorPage {

    /** the axes */
    private IAxis[] axes;

    /** the style combo */
    protected Combo styleCombo;

    /** the foreground button */
    protected ColorSelector foregroundButton;

    /** the line styles */
    protected LineStyle[] styles;

    /** the foreground colors */
    protected Color[] foregroundColors;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param direction
     *            the direction
     * @param title
     *            the title
     */
    public GridPage(Chart chart, Direction direction, String title) {
        super(chart, title, "Axes:");
        if (direction == Direction.X) {
            this.axes = chart.getAxisSet().getXAxes();
        } else if (direction == Direction.Y) {
            this.axes = chart.getAxisSet().getYAxes();
        }
        styles = new LineStyle[axes.length];
        foregroundColors = new Color[axes.length];
    }

    /*
     * @see AbstractSelectorPage#getListItems()
     */
    @Override
    protected String[] getListItems() {
        String[] items = new String[axes.length];
        for (int i = 0; i < items.length; i++) {
            items[i] = String.valueOf(axes[i].getId());
        }
        return items;
    }

    /*
     * @see AbstractSelectorPage#selectInitialValues()
     */
    @Override
    protected void selectInitialValues() {
        for (int i = 0; i < axes.length; i++) {
            styles[i] = axes[i].getGrid().getStyle();
            foregroundColors[i] = axes[i].getGrid().getForeground();
        }
    }

    /*
     * @see AbstractSelectorPage#updateControlSelections()
     */
    @Override
    protected void updateControlSelections() {
        styleCombo.setText(String.valueOf(styles[selectedIndex]));
        foregroundButton
                .setColorValue(foregroundColors[selectedIndex].getRGB());
    }

    /*
     * @see AbstractSelectorPage#addRightPanelContents(Composite)
     */
    @Override
    protected void addRightPanelContents(Composite parent) {
        addGridPanel(parent);
    }

    /**
     * Adds the grid panel.
     * 
     * @param parent
     *            the parent to add the grid panel
     */
    private void addGridPanel(Composite parent) {
        Composite group = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);
        group.setLayout(new GridLayout(2, false));

        createLabelControl(group, "Line style:");
        LineStyle[] values = LineStyle.values();
        String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            labels[i] = values[i].label;
        }
        styleCombo = createComboControl(group, labels);
        styleCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String value = styleCombo.getText();
                LineStyle selectedStyle = LineStyle.NONE;
                for (LineStyle style : LineStyle.values()) {
                    if (style.label.equals(value)) {
                        selectedStyle = style;
                    }
                }
                styles[selectedIndex] = selectedStyle;
            }
        });

        createLabelControl(group, "Color:");
        foregroundButton = createColorButtonControl(group);
        foregroundButton.addListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                foregroundColors[selectedIndex] = new Color(Display
                        .getDefault(), foregroundButton.getColorValue());
            }
        });
    }

    /*
     * @see AbstractPreferencePage#apply()
     */
    @Override
    public void apply() {
        for (int i = 0; i < axes.length; i++) {
            axes[i].getGrid().setStyle(styles[i]);
            axes[i].getGrid().setForeground(foregroundColors[i]);
        }
    }

    /*
     * @see PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        styles[selectedIndex] = LineStyle.DOT;
        foregroundColors[selectedIndex] = new Color(Display.getDefault(),
                Constants.GRAY);

        updateControlSelections();

        super.performDefaults();
    }
}
