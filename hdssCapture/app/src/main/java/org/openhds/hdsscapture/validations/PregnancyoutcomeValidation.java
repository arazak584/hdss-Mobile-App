package org.openhds.hdsscapture.validations;

import android.content.Context;
import android.view.View;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.openhds.hdsscapture.entity.Pregnancyoutcome;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PregnancyoutcomeValidation {

    private Context context;
    private Pregnancyoutcome item;
    private Date earliestEventDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public PregnancyoutcomeValidation(Context context, Pregnancyoutcome item) {
        this.context = context;
        this.item = item;
    }

    public PregnancyoutcomeValidation(Context context, Pregnancyoutcome item, Date earliestEventDate) {
        this.context = context;
        this.item = item;
        this.earliestEventDate = earliestEventDate;
    }

    public boolean validateAll() {
        List<ValidationError> errors = new ArrayList<>();

        // Date validations
        errors.addAll(validateDates());

        // Numeric constraint validations
        errors.addAll(validateNumericConstraints());

        // Business logic validations
        errors.addAll(validateBusinessLogic());

        if (!errors.isEmpty()) {
            // First, highlight all fields with errors
            highlightAllFieldsWithErrors(errors);

            // Then show the dialog
            showValidationErrors(errors);
            return false;
        }

//        if (!errors.isEmpty()) {
//            showValidationErrors(errors);
//            return false;
//        }

        return true;
    }

    /**
     * Highlight all fields that have errors
     */
    private void highlightAllFieldsWithErrors(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            highlightFieldWithError(error.fieldId, error.message);
        }
    }

    private List<ValidationError> validateDates() {
        List<ValidationError> errors = new ArrayList<>();

        try {
            Date currentDate = new Date();

            // Validate conception date against earliest event date
            if (earliestEventDate != null && item.getConceptionDate() != null && !item.getConceptionDate().trim().isEmpty()) {
                Date conceptionDate = sdf.parse(item.getConceptionDate().trim());
                if (conceptionDate.before(earliestEventDate)) {
                    errors.add(new ValidationError("conception_date_earliest",
                            "Conception Date Cannot Be Less than Earliest Event Date", "editText_conception"));
                }
            }

            // Validate outcome date and conception date
            if (item.getOutcomeDate() != null && !item.getOutcomeDate().trim().isEmpty() &&
                    item.getConceptionDate() != null && !item.getConceptionDate().trim().isEmpty()) {

                Date outcomeDate = sdf.parse(item.getOutcomeDate().trim());
                Date conceptionDate = sdf.parse(item.getConceptionDate().trim());

                // Outcome date cannot be in future
                if (outcomeDate.after(currentDate)) {
                    errors.add(new ValidationError("outcome_date_future",
                            "Date of Delivery Cannot Be a Future Date", "editText_outcomeDate"));
                }

                // Outcome date cannot be before conception date
                if (outcomeDate.before(conceptionDate)) {
                    errors.add(new ValidationError("outcome_before_conception",
                            "Delivery Date Cannot Be Less than Conception Date", "editText_conception"));
                }
            }

            // Validate pregnancy duration (1-12 months)
            if (item.getOutcomeDate() != null && !item.getOutcomeDate().trim().isEmpty() &&
                    item.getConceptionDate() != null && !item.getConceptionDate().trim().isEmpty()) {

                Date outcomeDate = sdf.parse(item.getOutcomeDate().trim());
                Date conceptionDate = sdf.parse(item.getConceptionDate().trim());

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(conceptionDate);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(outcomeDate);

                int yearDiff = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                int monthDiff = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                int dayDiff = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);

                if (dayDiff < 0) {
                    monthDiff--;
                }

                int totalDiffMonths = yearDiff * 12 + monthDiff;

                if (totalDiffMonths < 1 || totalDiffMonths > 12) {
                    errors.add(new ValidationError("pregnancy_duration_invalid",
                            "The difference between outcome and conception Date should be between 1 and 12 months",
                            "editText_conception"));
                }
            }

        } catch (ParseException e) {
            errors.add(new ValidationError("date_parse_error",
                    "Error parsing date", "editText_conception"));
        }

        return errors;
    }

    private List<ValidationError> validateNumericConstraints() {
        List<ValidationError> errors = new ArrayList<>();

        // Validate months pregnant before ANC (1-12)
        if (item.rec_anc !=null && item.rec_anc == 1) {
            validateRange(item.month_pg, 1, 12, "month_pg",
                    "Months Pregnant Before ANC Cannot be More than 12", errors);
        }

        // Validate number of ANC visits (1-20)
        if (item.rec_anc !=null && item.rec_anc == 1) {
            validateRange(item.num_anc, 1, 20, "num_anc",
                    "Maximum Number of ANC Visit is 20", errors);
        }

        // Validate months pregnant for IPT (1-12)
        if ((item.rec_anc !=null && item.rec_anc == 1) && (item.rec_ipt != null && item.rec_ipt == 1)) {
            validateRange(item.first_rec, 1, 12, "first_rec",
                    "Months Pregnant for IPT Cannot be More than 12", errors);
        }

        // Validate number of IPT taken (1-10)
        if ((item.rec_anc !=null && item.rec_anc == 1) && (item.rec_ipt != null && item.rec_ipt == 1)) {
            validateRange(item.many_ipt, 1, 10, "many_ipt",
                    "Number of IPT taken Cannot be More than 10", errors);
        }

        // Validate IPT given at minimum 3 months (13 weeks)
        if ((item.rec_anc !=null && item.rec_anc == 1) && (item.rec_ipt != null && item.rec_ipt == 1) && item.first_rec != null) {
            if (item.first_rec < 3) {
                errors.add(new ValidationError("ipt_minimum_months",
                        "IPT is given at 13 weeks (3 Months)", "first_rec"));
            }
        }

        return errors;
    }

    private List<ValidationError> validateBusinessLogic() {
        List<ValidationError> errors = new ArrayList<>();

        // Only doctors perform caesarian section
        if (item.ass_del != null && item.how_del != null) {
            if (item.ass_del != 1 && item.how_del == 2) {
                errors.add(new ValidationError("caesarian_doctor_only",
                        "Only Doctors Perform Caesarian Section", "ass_del"));
            }
        }

        // Number of live births cannot exceed total outcomes
        if (item.numberofBirths != null && item.numberOfLiveBirths != null) {
            if (item.numberofBirths < item.numberOfLiveBirths) {
                errors.add(new ValidationError("livebirths_exceeds_outcomes",
                        "Number of livebirths for this pregnancy cannot be more than number of outcomes",
                        "numberOfLiveBirths"));
            }
        }

        return errors;
    }

    // HELPER METHODS
    /**
     * Validates a field is within range (inclusive)
     */
    private void validateRange(Integer field, Integer min, Integer max, String fieldId, String message, List<ValidationError> errors) {
        if (field == null) return;

        boolean valid = true;
        if (min != null && field < min) valid = false;
        if (max != null && field > max) valid = false;

        if (!valid) {
            errors.add(new ValidationError(fieldId + "_constraint", message, fieldId));
        }
    }

    // ERROR DISPLAY
    private void showValidationErrors(List<ValidationError> errors) {
        if (errors.isEmpty()) return;

        if (errors.size() == 1) {
            // Single error - show simple dialog with "Go to Field" button
            ValidationError error = errors.get(0);
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Validation Error")
                    .setMessage(error.message)
                    .setPositiveButton("Go to Field", (dialog, which) -> {
                        highlightAndScrollToField(error.fieldId, error.message);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            // Multiple errors - show clickable list
            String[] errorMessages = new String[errors.size()];
            for (int i = 0; i < errors.size(); i++) {
                errorMessages[i] = (i + 1) + ". " + errors.get(i).message;
            }

            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Validation Errors (" + errors.size() + ")")
                    .setItems(errorMessages, (dialog, which) -> {
                        ValidationError selectedError = errors.get(which);
                        highlightAndScrollToField(selectedError.fieldId, selectedError.message);
                    })
                    .setPositiveButton("Close", null)
                    .show();
        }
    }

    /**
     * Highlight and scroll to field with error
     */
    private void highlightAndScrollToField(String fieldId, String errorMessage) {
        highlightFieldWithError(fieldId, errorMessage);
        scrollToField(fieldId);
    }

    /**
     * Highlight the field with an error indicator
     */
    private void highlightFieldWithError(String fieldId, String errorMessage) {
        try {
            // Get the field's resource ID
            int resId = context.getResources().getIdentifier(fieldId, "id", context.getPackageName());
            if (resId == 0) {
                // Try with common prefixes if direct match fails
                String[] prefixes = {"", "rg_", "spinner_", "et_", "til_", "editText_", "edit_text_"};
                for (String prefix : prefixes) {
                    resId = context.getResources().getIdentifier(prefix + fieldId, "id", context.getPackageName());
                    if (resId != 0) break;
                }
            }
            if (resId == 0) return;

            // Find the view
            View view = ((android.app.Activity) context).findViewById(resId);
            if (view == null) return;

            // Use provided error message or default
            String displayMessage = errorMessage != null ? errorMessage : "⚠ Validation Error";

            // Apply error styling based on view type
            if (view instanceof EditText) {
                EditText et = (EditText) view;
                et.setError(displayMessage);
                et.requestFocus();

            } else if (view instanceof TextView) {
                TextView tv = (TextView) view;
                tv.setError(displayMessage);
                tv.requestFocus();

            } else if (view instanceof Spinner) {
                Spinner spinner = (Spinner) view;
                // Create error TextView for Spinner
                showSpinnerError(spinner, displayMessage);
                spinner.requestFocus();
                highlightParentContainer(spinner);

            } else if (view instanceof org.openhds.searchSpinner.SearchableSpinner) {
                // Create error TextView for SearchableSpinner
                showSpinnerError(view, displayMessage);
                view.requestFocus();
                highlightParentContainer(view);

            } else if (view instanceof RadioGroup) {
                RadioGroup rg = (RadioGroup) view;
                // Set error on first radio button
                if (rg.getChildCount() > 0 && rg.getChildAt(0) instanceof RadioButton) {
                    RadioButton rb = (RadioButton) rg.getChildAt(0);
                    rb.setError(displayMessage);
                }
                rg.requestFocus();
                highlightParentContainer(rg);

            } else if (view instanceof CheckBox) {
                CheckBox cb = (CheckBox) view;
                cb.setError(displayMessage);
                cb.requestFocus();

            } else {
                // Generic view - just highlight parent
                view.requestFocus();
                highlightParentContainer(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show error message below a Spinner or SearchableSpinner
     */
    private void showSpinnerError(View spinnerView, String errorMessage) {
        try {
            ViewParent parent = spinnerView.getParent();
            if (!(parent instanceof android.view.ViewGroup)) return;

            android.view.ViewGroup parentView = (android.view.ViewGroup) parent;

            // Remove any existing error TextView
            int errorTextViewId = android.R.id.message; // Use a standard Android ID
            View existingError = parentView.findViewById(errorTextViewId);
            if (existingError != null) {
                parentView.removeView(existingError);
            }

            // Create error TextView
            TextView errorTextView = new TextView(context);
            errorTextView.setId(errorTextViewId);
            errorTextView.setText(errorMessage);
            errorTextView.setTextColor(android.graphics.Color.parseColor("#D32F2F")); // Red color
            errorTextView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);

            // Add error icon
            android.graphics.drawable.Drawable errorIcon = context.getResources().getDrawable(android.R.drawable.ic_dialog_alert);
            if (errorIcon != null) {
                errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());
                errorTextView.setCompoundDrawables(errorIcon, null, null, null);
                errorTextView.setCompoundDrawablePadding(8);
            }

            errorTextView.setPadding(16, 8, 16, 0);

            // Find the spinner's index in parent
            int spinnerIndex = parentView.indexOfChild(spinnerView);

            // Add error TextView right after the spinner
            android.view.ViewGroup.LayoutParams params = new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            parentView.addView(errorTextView, spinnerIndex + 1, params);

            // Remove error message after 5 seconds
            errorTextView.postDelayed(() -> {
                if (errorTextView.getParent() != null) {
                    parentView.removeView(errorTextView);
                }
            }, 5000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scroll to the field with error
     */
    private void scrollToField(String fieldId) {
        try {
            int resId = context.getResources().getIdentifier(fieldId, "id", context.getPackageName());
            if (resId == 0) {
                // Try with common prefixes
                String[] prefixes = {"", "rg_", "spinner_", "et_", "til_", "editText_", "edit_text_"};
                for (String prefix : prefixes) {
                    resId = context.getResources().getIdentifier(prefix + fieldId, "id", context.getPackageName());
                    if (resId != 0) break;
                }
            }
            if (resId == 0) return;

            final View view = ((android.app.Activity) context).findViewById(resId);
            if (view == null) return;

            // Find the ScrollView or NestedScrollView parent
            ViewParent parent = view.getParent();
            while (parent != null) {
                if (parent instanceof android.widget.ScrollView) {
                    final android.widget.ScrollView scrollView = (android.widget.ScrollView) parent;

                    // Post with delay to ensure layout is complete
                    view.post(() -> {
                        // Force layout update
                        view.requestLayout();

                        // Calculate position with better offset
                        view.postDelayed(() -> {
                            int[] location = new int[2];
                            view.getLocationOnScreen(location);

                            int[] scrollLocation = new int[2];
                            scrollView.getLocationOnScreen(scrollLocation);

                            // Calculate the Y position relative to ScrollView
                            int scrollY = location[1] - scrollLocation[1] + scrollView.getScrollY();

                            // Add offset to show field in middle of screen
                            int offset = scrollView.getHeight() / 4;
                            int targetY = Math.max(0, scrollY - offset);

                            scrollView.smoothScrollTo(0, targetY);
                        }, 100);
                    });
                    break;

                } else if (parent instanceof androidx.core.widget.NestedScrollView) {
                    final androidx.core.widget.NestedScrollView nestedScrollView = (androidx.core.widget.NestedScrollView) parent;

                    // Post with delay to ensure layout is complete
                    view.post(() -> {
                        // Force layout update
                        view.requestLayout();

                        // Calculate position with better offset
                        view.postDelayed(() -> {
                            int[] location = new int[2];
                            view.getLocationOnScreen(location);

                            int[] scrollLocation = new int[2];
                            nestedScrollView.getLocationOnScreen(scrollLocation);

                            // Calculate the Y position relative to NestedScrollView
                            int scrollY = location[1] - scrollLocation[1] + nestedScrollView.getScrollY();

                            // Add offset to show field in middle of screen
                            int offset = nestedScrollView.getHeight() / 4;
                            int targetY = Math.max(0, scrollY - offset);

                            nestedScrollView.smoothScrollTo(0, targetY);
                        }, 100);
                    });
                    break;

                } else if (parent instanceof androidx.recyclerview.widget.RecyclerView) {
                    androidx.recyclerview.widget.RecyclerView rv = (androidx.recyclerview.widget.RecyclerView) parent;
                    ViewParent rvParent = rv.getParent();
                    if (rvParent instanceof View) {
                        ((View) rvParent).requestLayout();
                    }
                    break;
                }
                parent = parent.getParent();
            }

            // Request focus with a delay to ensure scroll completes
            view.postDelayed(() -> view.requestFocus(), 400);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Highlight the parent container with a colored border
     */
    private void highlightParentContainer(View view) {
        try {
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                View parentView = (View) parent;

                // Save original background
                if (parentView.getTag(android.R.id.background) == null) {
                    parentView.setTag(android.R.id.background, parentView.getBackground());
                }

                // Create error background drawable
                android.graphics.drawable.GradientDrawable errorBg = new android.graphics.drawable.GradientDrawable();
                errorBg.setStroke(6, android.graphics.Color.parseColor("#F44336")); // Red border
                errorBg.setCornerRadius(8);
                parentView.setBackground(errorBg);

                // Remove error background after 10 seconds
                parentView.postDelayed(() -> {
                    android.graphics.drawable.Drawable original = (android.graphics.drawable.Drawable) parentView.getTag(android.R.id.background);
                    if (original != null) {
                        parentView.setBackground(original);
                    } else {
                        parentView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                    }
                }, 10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ValidationError {
        String code;
        String message;
        String fieldId;

        ValidationError(String code, String message, String fieldId) {
            this.code = code;
            this.message = message;
            this.fieldId = fieldId;
        }
    }
}