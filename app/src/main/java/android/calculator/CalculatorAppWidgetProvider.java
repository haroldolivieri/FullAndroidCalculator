package android.calculator;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;
import org.javia.arity.Util;

import java.util.ArrayList;
import java.util.List;

public class CalculatorAppWidgetProvider extends AppWidgetProvider {
    // private static RemoteViews views;
    private static final int[] buttonIds = {R.id.widget_0, R.id.widget_1,
            R.id.widget_2, R.id.widget_3, R.id.widget_4, R.id.widget_5,
            R.id.widget_6, R.id.widget_7, R.id.widget_8, R.id.widget_9,
            R.id.widget_point, R.id.widget_eq, R.id.widget_div,
            R.id.widget_mul, R.id.widget_sub, R.id.widget_add, R.id.widget_del,
            R.id.widget_clr};
    private static final String[] buttonValues = {"0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9", ".", "=", "/", "*", "-", "+", "DEL", "CLR"};
    private static final int BUTTON_INDEX_0 = 0;
    private static final int BUTTON_INDEX_1 = 1;
    private static final int BUTTON_INDEX_2 = 2;
    private static final int BUTTON_INDEX_3 = 3;
    private static final int BUTTON_INDEX_4 = 4;
    private static final int BUTTON_INDEX_5 = 5;
    private static final int BUTTON_INDEX_6 = 6;
    private static final int BUTTON_INDEX_7 = 7;
    private static final int BUTTON_INDEX_8 = 8;
    private static final int BUTTON_INDEX_9 = 9;
    private static final int BUTTON_INDEX_POINT = 10;
    private static final int BUTTON_INDEX_EQUAL = 11;
    private static final int BUTTON_INDEX_DIV = 12;
    private static final int BUTTON_INDEX_MUL = 13;
    private static final int BUTTON_INDEX_SUB = 14;
    private static final int BUTTON_INDEX_ADD = 15;
    private static final int BUTTON_INDEX_DEL = 16;
    private static final int BUTTON_INDEX_CLEAR = 17;
    private final static int EMPTY = 0;
    private final static int NAN = 1;
    private final static int RESULT = 2;
    private final static int SYNTAX = 3;
    private final static String TAG = "CalculatorWidget";
    private final static String _myaction = "calculator.appwidget.action.APPWIDGET_UPDATE";
    private static int theMaxWidgetID = -1;
    private static List<CalculatorWidgetEntity> mEntities = new ArrayList<CalculatorWidgetEntity>();

    private static CalculatorWidgetEntity queryEntityById(int widgetId) {
        for (int i = 0; i < mEntities.size(); i++) {
            if (mEntities.get(i).getViewId() == widgetId) {
                return mEntities.get(i);
            }
        }
        return null;
    }

    @Override
    public void onDisabled(Context context) {
        // TODO Auto-generated method stub
        super.onDisabled(context);
        mEntities.clear();
    }

    @Override
    public void onEnabled(Context context) {
        // TODO Auto-generated method stub
        super.onEnabled(context);
        ComponentName provdier = new ComponentName(context, this.getClass());
        int[] IDs = AppWidgetManager.getInstance(context).getAppWidgetIds(provdier);
        if (IDs != null && IDs.length > 0 && IDs[0] > theMaxWidgetID) {
            theMaxWidgetID = IDs[0] - 1;
        }

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // TODO Auto-generated method stub
        for (int i = 0; i < appWidgetIds.length; i++) {
            CalculatorWidgetEntity entity = queryEntityById(appWidgetIds[i]);
            if (entity != null) {
                mEntities.remove(entity);
            }
        }
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i(TAG, "onUpdate");
        Intent service = new Intent(context, CalculatorAppWidgetService.class);
        context.startService(service);
        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews view = new RemoteViews(context.getPackageName(),
                    R.layout.widget_main);
            if (appWidgetIds[appWidgetIds.length - 1] > theMaxWidgetID) {
                theMaxWidgetID = appWidgetIds[appWidgetIds.length - 1];
                CalculatorWidgetEntity entity = new CalculatorWidgetEntity();
                entity.setView(view);
                entity.setViewId(appWidgetIds[i]);
                entity.setFormula("");
                entity.setResult("");
                mEntities.add(entity);
            }
            updateWidget(context, view, appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds, view);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action) || action.equals(_myaction)) {
            int[] IDs = null;
            if (action.equals(_myaction)) {
                ComponentName provdier = new ComponentName(context, this.getClass());
                IDs = AppWidgetManager.getInstance(context).getAppWidgetIds(provdier);
            }
            Bundle extras = intent.getExtras();
            int[] appWidgetIds;
            if (extras != null || IDs != null) {
                if (extras != null)
                    appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                else appWidgetIds = IDs;
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    if (appWidgetIds.length == 1) {
                        this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                    } else if (appWidgetIds[0] < appWidgetIds[1]) {
                        for (int i = 0; i < appWidgetIds.length; i++) {
                            int[] tempappWidgetIds = {appWidgetIds[i]};
                            this.onUpdate(context, AppWidgetManager.getInstance(context), tempappWidgetIds);
                        }
                    } else {
                        for (int i = appWidgetIds.length - 1; i > -1; i--) {
                            int[] tempappWidgetIds = {appWidgetIds[i]};
                            this.onUpdate(context, AppWidgetManager.getInstance(context), tempappWidgetIds);
                        }
                    }


                }
            }
        } else {
            super.onReceive(context, intent);
        }
    }

    private void updateWidget(Context context, RemoteViews views, int widgetId) {
        ComponentName serviceName = new ComponentName(context,
                CalculatorAppWidgetService.class);
        for (int i = 0; i < buttonIds.length; i++) {
            Intent intent = new Intent();
            intent.putExtra("widgetId", widgetId);
            intent.setData(Uri.parse("custom:" + i));
            intent.setComponent(serviceName);
            int code = widgetId << 5;
            PendingIntent pi = PendingIntent.getService(context, code + i,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(buttonIds[i], pi);
        }
    }

    public static class CalculatorAppWidgetService extends Service {
        private static final int MAX_DIGITS = 12;
        private static final int ROUNDING_DIGITS = 2;
        private Symbols mSymbols;
        private CalculatorExpressionTokenizer mTokenizer;
        private boolean isEvalClick = false;
        private CalculatorWidgetEntity entity;

        @Override
        public void onCreate() {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setAction(_myaction);
            this.sendBroadcast(intent);
            super.onCreate();
        }

        @Override
        public void onDestroy() {
            // TODO Auto-generated method stub
            super.onDestroy();
        }

        @Override
        public IBinder onBind(Intent intent) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onStart(Intent intent, int startId) {
            // TODO Auto-generated method stub
            super.onStart(intent, startId);
            Log.i(TAG, "intent=" + intent);
            if (intent == null) {
                return;
            }
            int widgetId = intent.getIntExtra("widgetId", -1);
            if (widgetId == -1) {
                return;
            }

            entity = queryEntityById(widgetId);
            if (entity != null) {
            }
            if (entity == null) {
                return;
            }
            if (mTokenizer == null) {
                mTokenizer = new CalculatorExpressionTokenizer(this);
            }
            if (mSymbols == null) {
                mSymbols = new Symbols();
            }

            Uri data = intent.getData();
            if (data != null) {
                int index = Integer.parseInt(data.getSchemeSpecificPart());
                if (isEvalClick) {
                    if (index <= BUTTON_INDEX_9) {
                        entity.setFormula("");
                    }
                    isEvalClick = false;
                }
                checkInput(index);
                if (index == BUTTON_INDEX_EQUAL)
                    return;
            }
            if (entity.getView() != null) {
                int res = evaluate(entity.getFormula());
                entity.getView().setTextViewText(R.id.widget_formula,
                        entity.getFormula());
                if (res == RESULT) {
                    entity.getView().setTextViewText(R.id.widget_result,
                            entity.getResult());
                } else {
                    entity.getView().setTextViewText(R.id.widget_result, "");
                }
                AppWidgetManager.getInstance(this).updateAppWidget(widgetId,
                        entity.getView());
            }
        }

        public void checkInput(int buttonId) {
            switch (buttonId) {
                case BUTTON_INDEX_0:// 0
                    zeroClick();
                    break;
                case BUTTON_INDEX_POINT:// .
                    pointClick();
                    break;
                case BUTTON_INDEX_EQUAL:// =
                    evaluateClick();
                    break;
                case BUTTON_INDEX_DIV:// /
                case BUTTON_INDEX_MUL:// *
                case BUTTON_INDEX_ADD:// +
                    symbloClick(buttonId);
                    break;
                case BUTTON_INDEX_SUB:// -
                    subClick();
                    break;
                case BUTTON_INDEX_DEL:// DEL
                    deleteClick();
                    break;
                case BUTTON_INDEX_CLEAR:// CLR
                    entity.setFormula("");
                    break;
                default:
                    entity.setFormula(entity.getFormula() + buttonValues[buttonId]);
                    break;
            }
        }

        public void zeroClick() {
            entity.setFormula(entity.getFormula() + "0");
        }

        public void pointClick() {
            if (entity.getFormula().equalsIgnoreCase("")) {
                entity.setFormula("0.");
            } else {
                int pointLastIndex = entity.getFormula().lastIndexOf(".");
                int subLastIndex = entity.getFormula().lastIndexOf("−");// -
                int addLastIndex = entity.getFormula().lastIndexOf("+");// +
                int divLastIndex = entity.getFormula().lastIndexOf("÷");// ÷
                int mulLastIndex = entity.getFormula().lastIndexOf("×");// ×
                int maxIndex = Math.max(divLastIndex, mulLastIndex);
                maxIndex = Math.max(maxIndex, addLastIndex);
                maxIndex = Math.max(maxIndex, subLastIndex);
                if (maxIndex == -1 && pointLastIndex == -1) {
                    entity.setFormula(entity.getFormula() + ".");
                }
                if (pointLastIndex < maxIndex) {
                    if (maxIndex == entity.getFormula().length() - 1) {
                        entity.setFormula(entity.getFormula() + "0.");
                    } else {
                        entity.setFormula(entity.getFormula() + ".");
                    }
                }
            }

        }

        public void deleteClick() {
            if (entity.getFormula().length() > 0) {
                entity.setFormula(entity.getFormula().substring(0,
                        entity.getFormula().length() - 1));
            }
        }

        public void symbloClick(int buttonId) {
            while (entity.getFormula().length() > 0
                    && ".+−÷×".indexOf(entity.getFormula().charAt(
                    entity.getFormula().length() - 1)) != -1) {
                entity.setFormula(entity.getFormula().substring(0,
                        entity.getFormula().length() - 1));
            }
            if (entity.getFormula().length() > 0) {
                entity.setFormula(entity.getFormula() + buttonValues[buttonId]);
            }
        }

        public void evaluateClick() {
            if (entity.getFormula().equals(""))
                return;
            isEvalClick = true;
            int res = evaluate(entity.getFormula());
            if (res == NAN) {
                entity.setFormula("");
                entity.getView().setTextViewText(R.id.widget_result,
                        getText(R.string.error_nan));
            } else if (res == RESULT) {
                entity.setFormula(entity.getResult());
                entity.getView().setTextViewText(R.id.widget_formula,
                        entity.getResult());
            } else {
                entity.setFormula("");
                entity.getView().setTextViewText(R.id.widget_formula,
                        getText(R.string.error_syntax));
                entity.getView().setTextViewText(R.id.widget_result, "");
            }
            AppWidgetManager.getInstance(this).updateAppWidget(
                    entity.getViewId(), entity.getView());
        }

        public void subClick() {
            while (entity.getFormula().length() > 0
                    && ".+−".indexOf(entity.getFormula().charAt(
                    entity.getFormula().length() - 1)) != -1) {
                entity.setFormula(entity.getFormula().substring(0,
                        entity.getFormula().length() - 1));
            }
            entity.setFormula(entity.getFormula() + "-");
        }

        public int evaluate(String expr) {
            if (expr.equalsIgnoreCase("")) {
                return EMPTY;
            }
            entity.setFormula(mTokenizer.getLocalizedExpression(expr));
            expr = mTokenizer.getNormalizedExpression(expr);
            while (expr.length() > 0
                    && ".+-/*".indexOf(expr.charAt(expr.length() - 1)) != -1) {
                expr = expr.substring(0, expr.length() - 1);
            }
            try {
                double result = mSymbols.eval(expr);
                if (Double.isNaN(result)) {
                    return NAN;// "NaN";
                } else {
                    entity.setResult(mTokenizer.getLocalizedExpression(Util
                            .doubleToString(result, MAX_DIGITS, ROUNDING_DIGITS)));
                    return RESULT;// "result";
                }
            } catch (SyntaxException e) {
                return SYNTAX;// "syntax";
            }
        }

    }
}
