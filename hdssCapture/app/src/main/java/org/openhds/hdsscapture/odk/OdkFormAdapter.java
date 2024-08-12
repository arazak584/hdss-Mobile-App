package org.openhds.hdsscapture.odk;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.OdkViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OdkFormAdapter extends RecyclerView.Adapter<OdkFormAdapter.ViewHolder>{

    private final OdkFragment activity;
    private final LayoutInflater inflater;
    private static Individual selectedInd;
    private final Socialgroup socialgroup;
    private final FragmentActivity activity1;
    private Individual individual;
    private List<Form> formsList;
    private final OdkViewModel viewModel;
    private Context context;

    public OdkFormAdapter(Context context, OdkFragment activity, Individual selectedInd, Socialgroup socialgroup,OdkViewModel viewModel) {
        this.context = context;
        this.activity1 = activity.requireActivity();
        this.activity = activity;
        this.selectedInd = selectedInd;
        this.socialgroup = socialgroup;
        this.viewModel = viewModel;
        formsList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button button, hhid;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.button = view.findViewById(R.id.button);
            this.linearLayout = view.findViewById(R.id.odkForm);
        }
    }

    @NonNull
    @Override
    public OdkFormAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_form, parent, false);
        OdkFormAdapter.ViewHolder viewHolder = new OdkFormAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OdkFormAdapter.ViewHolder holder, int position) {
        final Form form = formsList.get(position);

        if (OdkFragment.selectedInd != null) {
            holder.button.setText(form.getFormName());
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOdkFormWithPrefillData(form);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return formsList.size();
    }

    public void setSelectedInd(Individual selectedInd) {
        formsList.clear();
        if (selectedInd != null) {

            try {
                List<Form> list = viewModel.find();

                if (list != null) {
                    formsList.addAll(list);
                }
                if (list.isEmpty()){
                    Toast.makeText(activity.getActivity(), "No Extra Forms ", Toast.LENGTH_SHORT).show();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    private void openOdkFormWithPrefillData(Form form) {
        if (OdkFragment.selectedInd != null) {
            // Get the form ID from the Form object
            String formId = form.getFormID();

            // Get the form URI using the form ID
            Uri formUri = getFormUriById(formId);

            if (formUri != null) {
                Intent odkIntent = new Intent(Intent.ACTION_EDIT, formUri);

                // Add extra data to the intent
                odkIntent.putExtra("first_name", OdkFragment.selectedInd.getFirstName());
                odkIntent.putExtra("last_name", OdkFragment.selectedInd.getLastName());
                odkIntent.putExtra("ext_id", OdkFragment.selectedInd.getExtId());
                odkIntent.putExtra("dob", OdkFragment.selectedInd.getDob() != null ? OdkFragment.selectedInd.getDob().toString() : "");
                odkIntent.putExtra("hhid", OdkFragment.selectedInd.getHohID());
                odkIntent.putExtra("compno", OdkFragment.selectedInd.getCompno());
                odkIntent.putExtra("village", OdkFragment.selectedInd.getVillage());

                // Check if an activity is available to handle the intent
                if (odkIntent.resolveActivity(activity.getActivity().getPackageManager()) != null) {
                    activity.startActivityForResult(odkIntent, OdkFragment.ODK_REQUEST_CODE);
                } else {
                    Toast.makeText(activity.getContext(), "ODK Collect not installed or incorrect URI", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity.getContext(), "Form not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity.getContext(), "No individual selected", Toast.LENGTH_SHORT).show();
        }
    }


    private Uri getFormUriById(String formId) {
        Uri formsUri = Uri.parse("content://org.odk.collect.android.provider.odk.forms/forms");
        Cursor cursor = null;
        Uri formUri = null;

        try {
            cursor = context.getContentResolver().query(
                    formsUri,
                    null,
                    "jrFormId=?",
                    new String[]{formId},
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("_id");
                if (idIndex != -1) {
                    String id = cursor.getString(idIndex);
                    formUri = Uri.withAppendedPath(formsUri, id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return formUri;
    }



}
