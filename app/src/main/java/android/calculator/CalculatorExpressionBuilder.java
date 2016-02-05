package android.calculator;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

public class CalculatorExpressionBuilder extends SpannableStringBuilder {

    private final CalculatorExpressionTokenizer mTokenizer;
    private boolean mIsEdited;

    public CalculatorExpressionBuilder(
            CharSequence text, CalculatorExpressionTokenizer tokenizer, boolean isEdited) {
        super(text);

        mTokenizer = tokenizer;
        mIsEdited = isEdited;
    }

    @Override
    public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart,
                                          int tbend) {
        if (start != length() || end != length()) {
            mIsEdited = true;
            return super.replace(start, end, tb, tbstart, tbend);
        }

        String appendExpr =
                mTokenizer.getNormalizedExpression(tb.subSequence(tbstart, tbend).toString());
        if (appendExpr.length() == 1) {
            final String expr = mTokenizer.getNormalizedExpression(toString());
            switch (appendExpr.charAt(0)) {
                case '.':
                    // don't allow two decimals in the same number
                    final int index = expr.lastIndexOf('.');
                    if (index != -1 && TextUtils.isDigitsOnly(expr.substring(index + 1, start))) {
                        appendExpr = "";
                    }
                    break;
                case '+':
                case '*':
                case '/':
                    // don't allow leading operator
                    if (start == 0) {
                        appendExpr = "";
                        break;
                    }

                    // don't allow multiple successive operators
                    while (start > 0 && "+-*/".indexOf(expr.charAt(start - 1)) != -1) {
                        --start;
                    }
                    // fall through
                case '-':
                    // don't allow -- or +-
                    if (start > 0 && "+-".indexOf(expr.charAt(start - 1)) != -1) {
                        --start;
                    }

                    // mark as edited since operators can always be appended
                    mIsEdited = true;
                    break;
                //add BUG_ID:DWYYL-466 zhaopenglin 20150513 (start)
                case '^':
                    if (length() == 0) {
                        appendExpr = "";
                    }
                    break;
                //add BUG_ID:DWYYL-466 zhaopenglin 20150513 (end)
                default:
                    break;
            }
        }

        // since this is the first edit replace the entire string
        if (!mIsEdited && appendExpr.length() > 0) {
            start = 0;
            mIsEdited = true;
        }

        appendExpr = mTokenizer.getLocalizedExpression(appendExpr);
        return super.replace(start, end, appendExpr, 0, appendExpr.length());
    }
}
