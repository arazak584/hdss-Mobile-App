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

import org.openhds.hdsscapture.entity.Pregnancy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PregnancyValidation {

    private Context context;
    private Pregnancy item;
    private Date earliestEventDate;
    private List<Pregnancy> allPregnancies;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public PregnancyValidation(Context context, Pregnancy item) {
        this.context = context;
        this.item = item;
    }

    public PregnancyValidation(Context context, Pregnancy item, Date earliestEventDate, List<Pregnancy> allPregnancies) {
        this.context = context;
        this.item = item;
        this.earliestEventDate = earliestEventDate;
        this.allPregnancies = allPregnancies;
    }

    public boolean validateAll() {
        List<ValidationError> errors = new ArrayList<>();

        // Date validations
        errors.addAll(validateDates());

        // Numeric constraint validations
        errors.addAll(validateNumericConstraints());

        // Chronological order validation with other pregnancies
        if (allPregnancies != null && !allPregnancies.isEmpty()) {
            errors.addAll(validateChronologicalOrder());
        }

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
    private void highlightAllFieldsWithErrors(List<PregnancyValidation.ValidationError> errors) {
        for (PregnancyValidation.ValidationError error : errors) {
            highlightFieldWithError(error.fieldId, error.message);
        }
    }

    private List<ValidationError> validateDates() {
        List<ValidationError> errors = new ArrayList<>();

        try {
            Date currentDate = new Date();

            // Validate conception date against earliest event date
            if (earliestEventDate != null && item.getRecordedDate() != null && !item.getRecordedDate().trim().isEmpty()) {
                Date conceptionDate = sdf.parse(item.getRecordedDate().trim());
                if (conceptionDate.before(earliestEventDate)) {
                    errors.add(new ValidationError("recorded_date_earliest",
                            "Conception Date Cannot Be Less than Earliest Event Date", "editText_recordedDate"));
                }
            }

            // Validate conception date is not in future
            if (item.getRecordedDate() != null && !item.getRecordedDate().trim().isEmpty()) {
                Date conceptionDate = sdf.parse(item.getRecordedDate().trim());
                if (conceptionDate.after(currentDate)) {
                    errors.add(new ValidationError("recorded_date_future",
                            "Date of Conception Cannot Be a Future Date", "editText_recordedDate"));
                }
            }

            // Validate last clinic visit date
            if (item.getRecordedDate() != null && !item.getRecordedDate().trim().isEmpty() &&
                    item.getLastClinicVisitDate() != null && !item.getLastClinicVisitDate().trim().isEmpty()) {
                Date conceptionDate = sdf.parse(item.getRecordedDate().trim());
                Date clinicDate = sdf.parse(item.getLastClinicVisitDate().trim());

                if (clinicDate.before(conceptionDate) || clinicDate.equals(conceptionDate)) {
                    errors.add(new ValidationError("clinic_date_invalid",
                            "Last Visit Date Cannot Be Less than or Equal to Conception Date", "editText_lastClinicVisitDate"));
                }

                if (clinicDate.after(currentDate)) {
                    errors.add(new ValidationError("clinic_date_invalids",
                            "Last Visit Date Cannot Be Future Date", "editText_lastClinicVisitDate"));
                }
            }

            // Validate outcome date
            if (item.getOutcome_date() != null && !item.getOutcome_date().trim().isEmpty()) {
                Date outcomeDate = sdf.parse(item.getOutcome_date().trim());

                // Outcome date cannot be in future
                if (outcomeDate.after(currentDate)) {
                    errors.add(new ValidationError("outcome_date_future",
                            "Date of Outcome Cannot Be a Future Date", "editText_outcomeDate"));
                }

                // Validate outcome date against conception date
                if (item.getRecordedDate() != null && !item.getRecordedDate().trim().isEmpty()) {
                    Date conceptionDate = sdf.parse(item.getRecordedDate().trim());
                    if (outcomeDate.before(conceptionDate) || outcomeDate.equals(conceptionDate)) {
                        errors.add(new ValidationError("outcome_date_before_conception",
                                "Delivery Date Cannot Be Less than Conception Date", "editText_outcomeDate"));
                    }
                }

                // Validate outcome date against last clinic visit
                if (item.getLastClinicVisitDate() != null && !item.getLastClinicVisitDate().trim().isEmpty()) {
                    Date clinicDate = sdf.parse(item.getLastClinicVisitDate().trim());
                    if (outcomeDate.before(clinicDate)) {
                        errors.add(new ValidationError("outcome_date_before_clinic",
                                "Date of Outcome Cannot Be Before Last Clinic Visit Date", "editText_outcomeDate"));
                    }
                }
            }

            // Validate pregnancy duration (1-12 months)
            if (item.getRecordedDate() != null && !item.getRecordedDate().trim().isEmpty() &&
                    item.getOutcome_date() != null && !item.getOutcome_date().trim().isEmpty()) {
                Date conceptionDate = sdf.parse(item.getRecordedDate().trim());
                Date outcomeDate = sdf.parse(item.getOutcome_date().trim());

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
                            "The difference between outcome and conception Date should be between 1 and 12 months", "editText_recordedDate"));
                }
            }

        } catch (ParseException e) {
            errors.add(new ValidationError("date_parse_error",
                    "Error parsing date", "editText_recordedDate"));
        }

        return errors;
    }

    private List<ValidationError> validateNumericConstraints() {
        List<ValidationError> errors = new ArrayList<>();

        // Validate pregnancy weeks (4-52)
        if (item.anteNatalClinic == 1) {
            validateRange(item.ageOfPregFromPregNotes, 4, 52, "ageOfPregFromPregNotes",
                    "Maximum Number of Weeks Allowed is 4 - 52", errors);
        }

        // Validate pregnancy months (1-12)
        if (item.anteNatalClinic == 1) {
            validateRange(item.estimatedAgeOfPreg, 1, 12, "estimatedAgeOfPreg",
                    "Maximum Number of Months Allowed is 12", errors);
        }

        //Validate first anc visit against months pregnant
        if(item.estimatedAgeOfPreg != null && item.first_rec !=null){
            if (item.estimatedAgeOfPreg < item.first_rec){
                errors.add(new ValidationError("first_anc_months_pregnant",
                        "Please check your answer. The first ANC visit cannot be after the current pregnancy duration.",
                        "first_rec"));
            }
        }

        // Validate months vs weeks consistency
        if (item.anteNatalClinic == 1 && item.estimatedAgeOfPreg != null && item.ageOfPregFromPregNotes != null) {
            int totalWeeksConvertedToMonths = item.ageOfPregFromPregNotes / 4;
            if (item.estimatedAgeOfPreg < totalWeeksConvertedToMonths) {
                errors.add(new ValidationError("pregnancy_duration_mismatch",
                        "Check Number of Months and weeks Pregnant", "ageOfPregFromPregNotes"));
            }
        }

        // Validate first recorded month (1-12)
        if (item.anteNatalClinic == 1) {
            validateRange(item.first_rec, 1, 12, "first_rec",
                    "Maximum Number of Months Allowed is 12", errors);
        }

        // Validate number of bednets (1-10)
        if (item.own_bnet == 1) {
            validateRange(item.how_many, 1, 10, "how_many",
                    "Maximum Number of Bednets is 10", errors);
        }

        // Validate pregnancy number (2-15)
        if (item.first_preg == 2) {
            validateRange(item.pregnancyNumber, 2, 15, "pregnancyNumber",
                    "Total Pregnancies Cannot be less than 2", errors);
        }

        // Validate ANC visits (1-20)
        if (item.anteNatalClinic == 1) {
            validateRange(item.anc_visits, 1, 20, "anc_visits",
                    "Maximum Number of ANC Visit is 20", errors);
        }

        return errors;
    }

    private List<ValidationError> validateChronologicalOrder() {
        List<ValidationError> errors = new ArrayList<>();

        if (item.pregnancyOrder <= 0 || item.getRecordedDate() == null || item.getRecordedDate().trim().isEmpty()) {
            return errors;
        }

        try {
            Date currentRecordedDate = sdf.parse(item.getRecordedDate().trim());
            Date currentOutcomeDate = (item.getOutcome_date() != null && !item.getOutcome_date().trim().isEmpty()) ?
                    sdf.parse(item.getOutcome_date().trim()) : null;

            // Find previous pregnancy
            Pregnancy previousPregnancy = null;
            for (Pregnancy p : allPregnancies) {
                if (p.pregnancyOrder == (item.pregnancyOrder - 1) &&
                        !p.uuid.equals(item.uuid)) {
                    previousPregnancy = p;
                    break;
                }
            }

            if (previousPregnancy != null) {
                // Validate against previous pregnancy's conception date
                if (previousPregnancy.recordedDate != null) {
                    Date prevRecordedDate = previousPregnancy.recordedDate;
                    if (currentRecordedDate.before(prevRecordedDate)) {
                        errors.add(new ValidationError("chronological_conception_before_prev",
                                "Pregnancy " + item.pregnancyOrder + " Conception Date cannot be before Pregnancy " +
                                        (item.pregnancyOrder - 1) + " Conception Date (" + sdf.format(prevRecordedDate) + ")",
                                "editText_recordedDate"));
                    }
                }

                // Validate against previous pregnancy's outcome date
                if (previousPregnancy.outcome_date != null) {
                    Date prevOutcomeDate = previousPregnancy.outcome_date;
                    if (currentRecordedDate.before(prevOutcomeDate)) {
                        errors.add(new ValidationError("chronological_conception_before_prev_outcome",
                                "Pregnancy " + item.pregnancyOrder + " Conception Date cannot be before Pregnancy " +
                                        (item.pregnancyOrder - 1) + " Outcome Date (" + sdf.format(prevOutcomeDate) + ")",
                                "editText_recordedDate"));
                    }

                    if (currentOutcomeDate != null && currentOutcomeDate.before(prevOutcomeDate)) {
                        errors.add(new ValidationError("chronological_outcome_before_prev_outcome",
                                "Pregnancy " + item.pregnancyOrder + " Outcome Date cannot be before Pregnancy " +
                                        (item.pregnancyOrder - 1) + " Outcome Date (" + sdf.format(prevOutcomeDate) + ")",
                                "editText_outcomeDate"));
                    }
                }
            }

            // Find next pregnancy (for editing earlier pregnancies)
            Pregnancy nextPregnancy = null;
            for (Pregnancy p : allPregnancies) {
                if (p.pregnancyOrder == (item.pregnancyOrder + 1) &&
                        !p.uuid.equals(item.uuid)) {
                    nextPregnancy = p;
                    break;
                }
            }

            if (nextPregnancy != null && nextPregnancy.recordedDate != null) {
                Date nextRecordedDate = nextPregnancy.recordedDate;

                // Current conception cannot be after next pregnancy's conception
                if (currentRecordedDate.after(nextRecordedDate)) {
                    errors.add(new ValidationError("chronological_conception_after_next",
                            "Pregnancy " + item.pregnancyOrder + " Conception Date cannot be after Pregnancy " +
                                    (item.pregnancyOrder + 1) + " Conception Date (" + sdf.format(nextRecordedDate) + ")",
                            "editText_recordedDate"));
                }

                // Current outcome cannot be after next pregnancy's conception
                if (currentOutcomeDate != null && currentOutcomeDate.after(nextRecordedDate)) {
                    errors.add(new ValidationError("chronological_outcome_after_next",
                            "Pregnancy " + item.pregnancyOrder + " Outcome Date cannot be after Pregnancy " +
                                    (item.pregnancyOrder + 1) + " Conception Date (" + sdf.format(nextRecordedDate) + ")",
                            "editText_outcomeDate"));
                }
            }

        } catch (ParseException e) {
            errors.add(new ValidationError("chronological_parse_error",
                    "Error parsing date in chronological validation", "editText_recordedDate"));
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

    /**
     * Validates a field is within range (exclusive min)
     */
    private void validateRangeExclusive(Integer field, Integer minExclusive, Integer max, String fieldId, String message, List<ValidationError> errors) {
        if (field == null) return;

        boolean valid = true;
        if (minExclusive != null && field <= minExclusive) valid = false;
        if (max != null && field > max) valid = false;

        if (!valid) {
            errors.add(new ValidationError(fieldId + "_constraint", message, fieldId));
        }
    }

    /**
     * Validates a field is less than a value
     */
    private void validateRangeLessThan(Integer field, Integer max, String fieldId, String message, List<ValidationError> errors) {
        if (field == null) return;

        if (field >= max) {
            errors.add(new ValidationError(fieldId + "_constraint", message, fieldId));
        }
    }

    /**
     * Validates adult age (>11 and <120)
     */
    private void validateAgeAdult(Integer field, String fieldId, String message, List<ValidationError> errors) {
        if (field == null) return;

        if (field <= 11 || field >= 120) {
            errors.add(new ValidationError(fieldId + "_constraint", message, fieldId));
        }
    }

    /**
     * Validates a field is within range OR matches special values (like 77, 88)
     */
    private void validateWithSpecialValues(Integer field, Integer min, Integer max, int[] specialValues, String fieldId, String message, List<ValidationError> errors) {
        if (field == null) return;

        // Check if it's a special value
        for (int special : specialValues) {
            if (field == special) return; // Valid
        }

        // Otherwise check range
        boolean valid = true;
        if (min != null && field < min) valid = false;
        if (max != null && field > max) valid = false;

        if (!valid) {
            errors.add(new ValidationError(fieldId + "_constraint", message, fieldId));
        }
    }

    /**
     * Validates a field against a dynamic maximum (from another field) with optional special values
     */
    private void validateWithMaxDynamic(Integer field, Integer min, Integer dynamicMax, int[] specialValues, String fieldId, String message, List<ValidationError> errors) {
        if (field == null) return;

        // Check if it's a special value (if provided)
        if (specialValues != null) {
            for (int special : specialValues) {
                if (field == special) return; // Valid
            }
        }

        // Check range with dynamic max
        boolean valid = true;
        if (min != null && field < min) valid = false;
        if (dynamicMax != null && field >= dynamicMax) valid = false;

        if (!valid) {
            errors.add(new ValidationError(fieldId + "_constraint", message, fieldId));
        }
    }

    /**
     * Helper: Validates that exclusive values (like "Don't know", "Refused")
     * are not selected together with other options
     */
    private boolean validateMultiSelectExclusive(Object field, String... exclusiveValues) {
        String value = getStringValue(field);
        if (value == null || value.isEmpty()) return true;

        String[] selections = value.split(",");
        if (selections.length > 1) {
            for (String s : selections) {
                String val = s.trim();
                for (String exclusive : exclusiveValues) {
                    if (val.equals(exclusive)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Helper: Converts field to String regardless of type
     */
    private String getStringValue(Object field) {
        if (field == null) return null;
        if (field instanceof String) return ((String) field).trim();
        if (field instanceof Integer) return field.toString();
        return field.toString();
    }

    private boolean isSelected(Integer field, int value) {
        return field != null && field == value;
    }

    private boolean isSelectedAny(Integer field, int... values) {
        if (field == null) return false;
        for (int value : values) {
            if (field == value) return true;
        }
        return false;
    }

    private boolean isEmpty(String field) {
        return field == null || field.trim().isEmpty();
    }

    private boolean isNotEmpty(String field) {
        return field != null && !field.trim().isEmpty();
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
                        // User clicked on an error - navigate to that field with its specific message
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
    private void highlightAndScrollToField(String fieldId) {
        highlightFieldWithError(fieldId, null);
        scrollToField(fieldId);
    }

    /**
     * Highlight and scroll to field with error and specific message
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

                // Remove error background after 5 seconds
                parentView.postDelayed(() -> {
                    android.graphics.drawable.Drawable original = (android.graphics.drawable.Drawable) parentView.getTag(android.R.id.background);
                    if (original != null) {
                        parentView.setBackground(original);
                    } else {
                        parentView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                    }
                }, 5000);
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