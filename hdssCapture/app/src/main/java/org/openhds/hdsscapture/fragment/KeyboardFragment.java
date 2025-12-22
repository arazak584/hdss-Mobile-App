package org.openhds.hdsscapture.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import org.openhds.searchSpinner.SearchableSpinner;

/**
 * Base Fragment class that handles automatic keyboard hiding
 * when moving between form fields, especially from EditText to Spinner.
 */
public abstract class KeyboardFragment extends Fragment {

    /**
     * Call this method in your fragment's onViewCreated to setup keyboard hiding
     * @param root The root view of your fragment
     */
    protected void setupKeyboardHiding(View root) {
        // Hide keyboard when touching non-input views
        if (!(root instanceof EditText)) {
            root.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideKeyboard();
                }
                return false;
            });
        }

        // Setup EditText focus change listeners
        if (root instanceof EditText) {
            setupEditTextFocusListener((EditText) root);
        }

        // Setup Spinner click listeners
        if (root instanceof Spinner || root instanceof SearchableSpinner) {
            root.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideKeyboard();
                }
                return false;
            });
        }

        // Recursively setup for child views
        if (root instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++) {
                setupKeyboardHiding(((ViewGroup) root).getChildAt(i));
            }
        }
    }

    /**
     * Setup focus change listener for EditText fields
     * Hides keyboard when EditText loses focus
     */
    private void setupEditTextFocusListener(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard();
            }
        });

        // Also hide keyboard when user presses Done/Next on keyboard
        editText.setOnEditorActionListener((v, actionId, event) -> {
            hideKeyboard();
            return false;
        });
    }

    /**
     * Hides the soft keyboard
     */
    protected void hideKeyboard() {
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * Shows the soft keyboard for a specific view
     * @param view The view to show keyboard for (usually an EditText)
     */
    protected void showKeyboard(View view) {
        if (view != null) {
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    /**
     * Alternative method: Setup keyboard hiding with a delay
     * This can be useful if immediate hiding causes issues
     */
    protected void setupKeyboardHidingWithDelay(View root, long delayMillis) {
        if (!(root instanceof EditText)) {
            root.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.postDelayed(this::hideKeyboard, delayMillis);
                }
                return false;
            });
        }

        if (root instanceof EditText) {
            setupEditTextFocusListener((EditText) root);
        }

        if (root instanceof Spinner || root instanceof SearchableSpinner) {
            root.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.postDelayed(this::hideKeyboard, delayMillis);
                }
                return false;
            });
        }

        if (root instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++) {
                setupKeyboardHidingWithDelay(((ViewGroup) root).getChildAt(i), delayMillis);
            }
        }
    }
}