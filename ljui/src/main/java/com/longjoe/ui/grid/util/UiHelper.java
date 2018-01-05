package com.longjoe.ui.grid.util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.longjoe.ui.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Created by Jafin on 2016/12/31.
 */
public class UiHelper {
    public final static int STRING = 0;
    public final static int FLOAT = 1;
    public final static int DOUBLE = 2;
    public final static int INT = 3;

    public static int getType(Object obj) {
        if (obj instanceof Float) {
            return FLOAT;
        } else if (obj instanceof Double) {
            return DOUBLE;
        } else if (obj instanceof Integer) {
            return INT;
        } else {
            return STRING;
        }
    }

    public static Object getQty(Object obj, String content) {
        if (obj instanceof Float) {
            return Float.parseFloat(content);
        } else if (obj instanceof Double) {
            return Double.parseDouble(content);
        } else if (obj instanceof Integer) {
            return Integer.parseInt(content);
        } else {
            return content;
        }
    }

    public interface OnLostFocusListener {
        void onLostFocus(Object qty);
    }

    public interface OnGetFocusListener {
        void onGetFocus();
    }

    public static void setViewEnable(Activity activity, final View view, final boolean enable) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    view.setEnabled(enable);
                }
            }
        });
    }

    public static void setClearListener(final EditText edit) {
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (edit.getOnFocusChangeListener() != null) {
                    edit.getOnFocusChangeListener().onFocusChange(v, hasFocus);
                }
                if (hasFocus) {
                    edit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete, 0);
                }
            }
        });

        edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Drawable drawable = edit.getCompoundDrawables()[2];
                    if (drawable == null) {
                        return false;
                    } else if (event.getX() > edit.getWidth() - drawable.getBounds().width()) {
                        edit.setText("");
                    }
                }
                return true;
            }
        });
    }

    public static void setFocusListener(final EditText edit, final Object source, final OnLostFocusListener listener,
                                        final OnGetFocusListener get) {
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String content = edit.getText().toString().trim();
                    try {
                        listener.onLostFocus(getQty(source, content));
                    } catch (NumberFormatException e) {
                        edit.setText(source.toString());
                    }
                } else {
                    if (get != null) {
                        get.onGetFocus();
                    }
                    edit.setText("");
                    if (getType(source) == DOUBLE) {
                        edit.setHint(StringUtils.formatDouble((Double) source));
                    } else if (getType(source) == FLOAT) {
                        edit.setHint(StringUtils.formatFloat((Float) source));
                    }
                    edit.setHint(source.toString());
                }
            }
        });
    }

    public static void setFocusListener(final EditText edit, final Object source, final OnLostFocusListener listener) {
        setFocusListener(edit, source, listener, null);
    }

    public static void setFocusListener(final EditText edit, final Method method, final Object o, final
    OnLostFocusListener listener) throws Exception {
        setFocusListener(edit, method, o, listener, null);
    }

    public static void setFocusListener(final EditText edit, final Method method, final Object o, final
    OnLostFocusListener listener, final OnGetFocusListener get) throws Exception {
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String content = edit.getText().toString().trim();
                    try {
                        double qty = Double.parseDouble(content);
                        listener.onLostFocus(qty);
                    } catch (NumberFormatException e) {
                        try {
                            double invoke = (double) method.invoke(o);
                            edit.setText(StringUtils.formatDouble(invoke));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    if (get != null) {
                        get.onGetFocus();
                    }
                    edit.setText("");
                    try {
                        double invoke = (double) method.invoke(o);
                        edit.setHint(StringUtils.formatDouble(invoke));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public static void setFocusListener(final EditText edit, final Field field, final Object o, final
    OnLostFocusListener listener, final OnGetFocusListener get) throws Exception {
       /* edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String content = edit.getText().toString().trim();
                    try {
                        Object qty = null;
                        if (field.getFieldType().equals("double")) {
                            qty = Double.parseDouble(content);
                        } else if (field.getFieldType().equals("int")) {
                            qty = Integer.parseInt(content);
                        } else if (field.getFieldType().equals("float")) {
                            qty = Float.parseFloat(content);
                        }
                        listener.onLostFocus(qty);
                    } catch (NumberFormatException e) {
                        try {
                            if (field.getFieldType().getName().equals("double")) {
                                double invoke = (double) field.getValue(o);
                                edit.setText(StringUtils.formatDouble(invoke));
                            } else {
                                edit.setText(field.getValue(o).toString());
                            }

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    if (getValue != null) {
                        getValue.onGetFocus();
                    }
                    edit.setText("");
                    try {
                        if (field.getFieldType().getName().equals("double")) {
                            double invoke = (double) field.getValue(o);
                            edit.setText(StringUtils.formatDouble(invoke));
                        } else {
                            edit.setText(field.getValue(o).toString());
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });*/
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(get!=null){
                        get.onGetFocus();
                    }
                    edit.setText("");
                    try {
                        String hint = field.get(o).toString();
                        edit.setHint(hint);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        String content = edit.getText().toString();
                        Object qty = null;
                        if (field.getType().getName().equals("double")) {
                            qty = Double.parseDouble(content);
                        } else if (field.getType().getName().equals("int")) {
                            qty = Integer.parseInt(content);
                        } else if (field.getType().getName().equals("float")) {
                            qty = Float.parseFloat(content);
                        }
                        if(listener!=null){
                            listener.onLostFocus(qty);
                        }
                    } catch (NumberFormatException e) {
                        try {
                            String hint = field.get(o).toString();
                            edit.setText(hint);
                        } catch (IllegalAccessException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
