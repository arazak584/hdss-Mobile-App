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

import org.openhds.hdsscapture.entity.Relationship;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RelationshipValidation {

    private Context context;
    private Relationship item;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public RelationshipValidation(Context context, Relationship item) {
        this.context = context;
        this.item = item;
    }

    public boolean validateAll() {
        List<ValidationError> errors = new ArrayList<>();

        // Date validations
        errors.addAll(validateDates());

        // Numeric constraint validations
        errors.addAll(validateNumericConstraints());

        if (!errors.isEmpty()) {
            showValidationErrors(errors);
            return false;
        }

        return true;
    }

    private List<ValidationError> validateDates() {
        List<ValidationError> errors = new ArrayList<>();

        try {
            Date currentDate = new Date();

            // Validate start date against DOB (woman's date of birth)
            if (item.getStartDate() != null && item.getDob() != null) {
                Date startDate = sdf.parse(item.getStartDate().trim());
                Date dob = sdf.parse(item.getDob().trim());

                if (dob.after(startDate)) {
                    errors.add(new ValidationError("start_date_before_dob",
                            "Start Date Cannot Be Less than Date of Birth", "relStartDate"));
                }
            }

            // Validate start date is not in future
            if (item.getStartDate() != null) {
                Date startDate = sdf.parse(item.getStartDate().trim());
                if (startDate.after(currentDate)) {
                    errors.add(new ValidationError("start_date_future",
                            "Date Cannot Be a Future Date", "relStartDate"));
                }
            }

            // Validate end date against start date
            if (item.getStartDate() != null && item.getEndDate() != null) {
                Date startDate = sdf.parse(item.getStartDate().trim());
                Date endDate = sdf.parse(item.getEndDate().trim());

                if (endDate.before(startDate)) {
                    errors.add(new ValidationError("end_date_before_start",
                            "End Date Cannot Be Less than Start Date", "relEndDate"));
                }
            }

        } catch (ParseException e) {
            errors.add(new ValidationError("date_parse_error",
                    "Error parsing date", "relStartDate"));
        }

        return errors;
    }

    private List<ValidationError> validateNumericConstraints() {
        List<ValidationError> errors = new ArrayList<>();

        // Validate total biological children vs children from this marriage
        if (item.tnbch != null && item.nchdm != null) {
            if (item.tnbch < item.nchdm) {
                errors.add(new ValidationError("children_mismatch",
                        "Number of Biological Children Cannot be Less than Children from this Marriage", "nchdm"));
            }
        }

        // Validate number of wives (must be >= 2 if polygamous)
        if (item.polygamous == 1 && item.nwive != null) {
            validateRange(item.nwive, 2, null, "nwive",
                    "Cannot be less than 2", errors);
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
                String[] prefixes = {"", "rg_", "spinner_", "et_", "til_", "editText", "button"};
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
                spinner.requestFocus();
                highlightParentContainer(spinner);

            } else if (view instanceof org.openhds.searchSpinner.SearchableSpinner) {
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

    /**
     * Scroll to the field with error
     */
    private void scrollToField(String fieldId) {
        try {
            int resId = context.getResources().getIdentifier(fieldId, "id", context.getPackageName());
            if (resId == 0) {
                // Try with common prefixes
                String[] prefixes = {"", "rg_", "spinner_", "et_", "til_", "editText", "button"};
                for (String prefix : prefixes) {
                    resId = context.getResources().getIdentifier(prefix + fieldId, "id", context.getPackageName());
                    if (resId != 0) break;
                }
            }
            if (resId == 0) return;

            View view = ((android.app.Activity) context).findViewById(resId);
            if (view != null) {
                // Find the ScrollView or NestedScrollView parent
                ViewParent parent = view.getParent();
                while (parent != null) {
                    if (parent instanceof android.widget.ScrollView) {
                        final android.widget.ScrollView scrollView = (android.widget.ScrollView) parent;
                        scrollView.post(() -> {
                            scrollView.smoothScrollTo(0, view.getTop() - 100);
                        });
                        break;
                    } else if (parent instanceof androidx.core.widget.NestedScrollView) {
                        final androidx.core.widget.NestedScrollView nestedScrollView = (androidx.core.widget.NestedScrollView) parent;
                        nestedScrollView.post(() -> {
                            nestedScrollView.smoothScrollTo(0, view.getTop() - 100);
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
                view.postDelayed(() -> view.requestFocus(), 300);
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