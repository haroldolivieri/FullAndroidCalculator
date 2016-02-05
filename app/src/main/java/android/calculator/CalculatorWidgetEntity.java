package android.calculator;

import android.widget.RemoteViews;

public class CalculatorWidgetEntity {

    private RemoteViews view;
    private int viewId;
    private String formula;
    private String result;

    public RemoteViews getView() {
        return view;
    }

    public void setView(RemoteViews view) {
        this.view = view;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
