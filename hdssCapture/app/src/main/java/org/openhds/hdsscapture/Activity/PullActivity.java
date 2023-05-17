package org.openhds.hdsscapture.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.AppJson;
import org.openhds.hdsscapture.Dao.ApiDao;
import org.openhds.hdsscapture.Dao.DemographicDao;
import org.openhds.hdsscapture.Dao.HdssSociodemoDao;
import org.openhds.hdsscapture.Dao.IndividualDao;
import org.openhds.hdsscapture.Dao.LocationDao;
import org.openhds.hdsscapture.Dao.PregnancyDao;
import org.openhds.hdsscapture.Dao.RelationshipDao;
import org.openhds.hdsscapture.Dao.ResidencyDao;
import org.openhds.hdsscapture.Dao.SocialgroupDao;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Repositories.IndividualRepository;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.HierarchyViewModel;
import org.openhds.hdsscapture.Viewmodel.RoundViewModel;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.wrapper.DataWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PullActivity extends AppCompatActivity {

    private ApiDao dao;
    private ProgressDialog progress;
    private IndividualRepository individualRepository;
    private IndividualDao individualDao;
    private LocationDao locationDao;
    private ResidencyDao residencyDao;
    private RelationshipDao relationshipDao;
    private SocialgroupDao socialgroupDao;
    private PregnancyDao pregnancyDao;
    private DemographicDao demographicDao;
    private HdssSociodemoDao hdssSociodemoDao;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull);


        progress = new ProgressDialog(PullActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        AppJson api = AppJson.getInstance(this);
        dao = api.getJsonApi();

        /*final Button button_SyncCodeBook = findViewById(R.id.button_SyncCodeBook);
        button_SyncCodeBook.setOnClickListener(v -> {
            final TextView textView_SyncCodeBook = findViewById(R.id.textView_SyncCodebook);
            textView_SyncCodeBook.setText("");
            progress.show();

            progress.setMessage("Updating Data Dictionary...");
            final CodeBookViewModel viewModel = new ViewModelProvider(this).get(CodeBookViewModel.class);

            Call<DataWrapper<CodeBook>> c_callable = dao.getCodeBook();
            c_callable.enqueue(new Callback<DataWrapper<CodeBook>>() {
                @Override
                public void onResponse(Call<DataWrapper<CodeBook>> call, Response<DataWrapper<CodeBook>> response) {
                    CodeBook[] d = response.body().getData().toArray(new CodeBook[0]);
                    viewModel.add(d);
                    progress.dismiss();
                    textView_SyncCodeBook.setText("Codebook Updated 100%!");
                    textView_SyncCodeBook.setTextColor(Color.rgb(0, 114, 133));
                }

                @Override
                public void onFailure(Call<DataWrapper<CodeBook>> call, Throwable t) {

                }
            });
        });*/

        //Sync LocationHierarchy
        final Button syncHierarchyData = findViewById(R.id.button_SyncHierarchyData);
        syncHierarchyData.setOnClickListener(v -> {

            final TextView textView_SyncHierarchyData = findViewById(R.id.textView_SyncHierarchyData);
            textView_SyncHierarchyData.setText("");
            progress.show();

                      //when region is synched, sync Hierarchy
                      progress.setMessage("Updating Hierarchy...");
                      final HierarchyViewModel hierarchyViewModel = new ViewModelProvider(PullActivity.this).get(HierarchyViewModel.class);
                      Call<DataWrapper<Hierarchy>> c_callable = dao.getAllHierarchy();
                      c_callable.enqueue(new Callback<DataWrapper<Hierarchy>>() {
                      @Override
                      public void onResponse(Call<DataWrapper<Hierarchy>> call, Response<DataWrapper<Hierarchy>> response) {
                      Hierarchy[] hierarchies = response.body().getData().toArray(new Hierarchy[0]);
                      hierarchyViewModel.add(hierarchies);


                       //Sync Round
                      progress.setMessage("Updating Round...");
                      final RoundViewModel round = new ViewModelProvider(PullActivity.this).get(RoundViewModel.class);
                      Call<DataWrapper<Round>> c_callable = dao.getRound();
                       c_callable.enqueue(new Callback<DataWrapper<Round>>() {
                       @Override
                       public void onResponse(Call<DataWrapper<Round>> call, Response<DataWrapper<Round>> response) {
                       Round[] i = response.body().getData().toArray(new Round[0]);
                       round.add(i);

                       //Sync Round
                       progress.setMessage("Updating Codebook...");
                       final CodeBookViewModel codeBook = new ViewModelProvider(PullActivity.this).get(CodeBookViewModel.class);
                       Call<DataWrapper<CodeBook>> c_callable = dao.getCodeBook();
                       c_callable.enqueue(new Callback<DataWrapper<CodeBook>>() {
                       @Override
                       public void onResponse(Call<DataWrapper<CodeBook>> call, Response<DataWrapper<CodeBook>> response) {
                       CodeBook[] co = response.body().getData().toArray(new CodeBook[0]);
                       codeBook.add(co);


                        progress.dismiss();
                         textView_SyncHierarchyData.setText("Codebook and Locationhierarchy updated Successfully");
                                textView_SyncHierarchyData.setTextColor(Color.rgb(0, 114, 133));
                            }


                          @Override
                          public void onFailure(Call<DataWrapper<CodeBook>> call, Throwable t) {
                           progress.dismiss();
                           textView_SyncHierarchyData.setText("Codebook Sync Error!");
                            textView_SyncHierarchyData.setTextColor(Color.RED);
                            }
                            });
                            }

                                                        @Override
                                                        public void onFailure(Call<DataWrapper<Round>> call, Throwable t) {
                                                            progress.dismiss();
                                                            textView_SyncHierarchyData.setText("Round Sync Error!");
                                                            textView_SyncHierarchyData.setTextColor(Color.RED);
                                                        }
                                                    });
                                                }


                                        @Override
                                        public void onFailure(Call<DataWrapper<Hierarchy>> call, Throwable t) {
                                            progress.dismiss();
                                            textView_SyncHierarchyData.setText("Hierarchy Sync Error!");
                                            textView_SyncHierarchyData.setTextColor(Color.RED);
                                        }
                                    });


        });

        //Sync Zipped Individual
        final TextView textView_SyncIndividual = findViewById(R.id.textView_SyncIndividualData);
        final Button button_DownloadIndividual = findViewById(R.id.button_SyncIndividualData);
        button_DownloadIndividual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                progress.setMessage("Downloading Individual Dataset...");
                Call<ResponseBody> call = dao.downloadZipFile();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            //progress.setMessage("Downloading zip file...");
                            progress.setMessage("Contacting Server...");
                            try {
                                // Read the response body into a file
                                progress.setMessage("Downloading Zip File...");
                                InputStream inputStream = response.body().byteStream();
                                File file = new File(getExternalCacheDir(), "individual.zip");
                                if (file.exists()) {
                                    file.delete();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(response.body().bytes());
                                fileOutputStream.close();
                                //Toast.makeText(PullActivity.this, "Download success", Toast.LENGTH_SHORT).show();
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                // Unzip the file
                                progress.setMessage("Unzipping File...");
                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    String fileName = zipEntry.getName();
                                    File newFile = new File(getExternalCacheDir() + File.separator + fileName);
                                    FileOutputStream fos = new FileOutputStream(newFile);
                                    int len;
                                    while ((len = zipInputStream.read(buffer)) > 0) {
                                        fos.write(buffer, 0, len);
                                    }
                                    fos.close();
                                    zipEntry = zipInputStream.getNextEntry();
                                    progress.dismiss();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                individualDao = appDatabase.individualDao();
                                // Import the unzipped CSV file into the Room database
                                if (individualDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "individual.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("individual_uuid").addColumn("dob").addColumn("dobAspect").addColumn("extId")
                                            .addColumn("father_uuid").addColumn("firstName").addColumn("fw_uuid").addColumn("gender").addColumn("ghanacard").addColumn("insertDate")
                                            .addColumn("lastName").addColumn("mother_uuid").addColumn("otherName").build();
                                    MappingIterator<Individual> iterator = mapper.readerFor(Individual.class).with(schema).readValues(unzippedFile);
                                    progress.show();
                                    AtomicInteger counts = new AtomicInteger();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<Individual> individuals = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            Individual individual = iterator.next();
                                            if (individual != null) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        progress.setMessage("Saving " + counts.incrementAndGet() + " of the Individuals");
                                                    }
                                                });

                                                individuals.add(individual);
                                                batchCount++;
                                                if (batchCount == batchSize) {
                                                    individualDao.insert(individuals);
                                                    individuals.clear();
                                                    batchCount = 0;
                                                }
                                            }
                                        }
                                        if (batchCount > 0) {
                                            individualDao.insert(individuals);
                                        }
                                        progress.dismiss();
                                        textView_SyncIndividual.setText("Total Individuals Saved: " + counts);
                                        textView_SyncIndividual.setTextColor(Color.rgb(0, 114, 133));
                                    });
                                }
                                File cacheDir = getExternalCacheDir();
                                if (cacheDir != null && cacheDir.isDirectory()) {
                                    File[] files = cacheDir.listFiles();
                                    for (File filez : files) {
                                        String fileName = filez.getName();
                                        if (fileName.endsWith(".zip") || fileName.endsWith(".csv")) {
                                            filez.delete();
                                        }
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show error message
                        progress.dismiss();
                        textView_SyncIndividual.setText("Individual Download Error! Retry or Contact Administrator");
                        textView_SyncIndividual.setTextColor(Color.RED);
                    }


                });


            }

        });


        //Sync Zipped Locations
        final Button button_DownloadLocation = findViewById(R.id.button_SyncLocationData);
        final TextView textView_SyncLocation = findViewById(R.id.textView_SyncLocationData);
        button_DownloadLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progress.show();
                progress.setMessage("Downloading Locations...");
                Call<ResponseBody> call = dao.downloadLocation();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            try {
                                // Read the response body into a file
                                InputStream inputStream = response.body().byteStream();
                                File file = new File(getExternalCacheDir(), "location.zip");
                                if (file.exists()) {
                                    file.delete();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(response.body().bytes());
                                fileOutputStream.close();
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                // Unzip the file
                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    String fileName = zipEntry.getName();
                                    File newFile = new File(getExternalCacheDir() + File.separator + fileName);
                                    FileOutputStream fos = new FileOutputStream(newFile);
                                    int len;
                                    while ((len = zipInputStream.read(buffer)) > 0) {
                                        fos.write(buffer, 0, len);
                                    }
                                    fos.close();
                                    zipEntry = zipInputStream.getNextEntry();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                locationDao = appDatabase.locationDao();
                                // Import the unzipped CSV file into the Room database
                                if (locationDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "location.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("compextId").addColumn("accuracy").addColumn("compno")
                                            .addColumn("fw_uuid").addColumn("insertDate").addColumn("latitude").addColumn("locationLevel_uuid").addColumn("locationName")
                                            .addColumn("locationType").addColumn("location_uuid").addColumn("longitude").addColumn("status").build();
                                    MappingIterator<Locations> iterator = mapper.readerFor(Locations.class).with(schema).readValues(unzippedFile);
                                    progress.setCancelable(true);
                                    progress.setCanceledOnTouchOutside(true);
                                    progress.show();
                                    AtomicInteger counts = new AtomicInteger();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<Locations> locations = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            Locations location = iterator.next();
                                            if (location != null) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        progress.setMessage("Saving " + counts.incrementAndGet() + " of the Compounds");
                                                    }
                                                });
                                                locations.add(location);
                                                batchCount++;
                                                if (batchCount == batchSize) {
                                                    locationDao.insert(locations);
                                                    locations.clear();
                                                    batchCount = 0;
                                                }
                                            }
                                        }
                                        if (batchCount > 0) {
                                            locationDao.insert(locations);
                                        }
                                        progress.dismiss();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                textView_SyncLocation.setText("Total Compounds Saved: " + counts);
                                                textView_SyncLocation.setTextColor(Color.rgb(0, 114, 133));
                                            }
                                        });

                                    });
                                }
                                File cacheDir = getExternalCacheDir();
                                if (cacheDir != null && cacheDir.isDirectory()) {
                                    File[] files = cacheDir.listFiles();
                                    for (File filez : files) {
                                        String fileName = filez.getName();
                                        if (fileName.endsWith(".zip") || fileName.endsWith(".csv")) {
                                            filez.delete();
                                        }
                                    }
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show error message
                        progress.dismiss();
                        textView_SyncLocation.setText("Locations Download Error! Retry or Contact Administrator");
                        textView_SyncLocation.setTextColor(Color.RED);
                    }


                });


            }

        });


        //Sync Zipped Residency
        final Button button_DownloadResidency = findViewById(R.id.button_SyncResidency);
        final TextView textView_SyncResidency = findViewById(R.id.textView_SyncResidency);
        button_DownloadResidency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                progress.setMessage("Downloading Residency...");
                Call<ResponseBody> call = dao.downloadResidency();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                // Read the response body into a file
                                InputStream inputStream = response.body().byteStream();
                                File file = new File(getExternalCacheDir(), "residency.zip");
                                if (file.exists()) {
                                    file.delete();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(response.body().bytes());
                                fileOutputStream.close();
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                // Unzip the file
                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    String fileName = zipEntry.getName();
                                    File newFile = new File(getExternalCacheDir() + File.separator + fileName);
                                    FileOutputStream fos = new FileOutputStream(newFile);
                                    int len;
                                    while ((len = zipInputStream.read(buffer)) > 0) {
                                        fos.write(buffer, 0, len);
                                    }
                                    fos.close();
                                    zipEntry = zipInputStream.getNextEntry();
                                    progress.dismiss();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                residencyDao = appDatabase.residencyDao();
                                // Import the unzipped CSV file into the Room database
                                if (residencyDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "residency.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("residency_uuid").addColumn("endDate").addColumn("endType")
                                            .addColumn("fw_uuid").addColumn("individual_uuid").addColumn("insertDate").addColumn("location_uuid")
                                            .addColumn("rltn_head").addColumn("socialgroup_uuid").addColumn("startDate").addColumn("startType").build();
                                    MappingIterator<Residency> iterator = mapper.readerFor(Residency.class).with(schema).readValues(unzippedFile);
                                    progress.show();
                                    AtomicInteger counts = new AtomicInteger();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<Residency> residencies = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            Residency residency = iterator.next();
                                            if (residency != null) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        progress.setMessage("Saving " + counts.incrementAndGet() + " of the Residency");
                                                    }
                                                });
                                                residencies.add(residency);
                                                batchCount++;
                                                if (batchCount == batchSize) {
                                                    residencyDao.insert(residencies);
                                                    residencies.clear();
                                                    batchCount = 0;
                                                }
                                            }
                                        }
                                        if (batchCount > 0) {
                                            residencyDao.insert(residencies);
                                        }
                                        progress.dismiss();
                                        textView_SyncResidency.setText("Total Residency Saved: " + counts);
                                        textView_SyncResidency.setTextColor(Color.rgb(0, 114, 133));
                                    });
                                }

                                File cacheDir = getExternalCacheDir();
                                if (cacheDir != null && cacheDir.isDirectory()) {
                                    File[] files = cacheDir.listFiles();
                                    for (File filez : files) {
                                        String fileName = filez.getName();
                                        if (fileName.endsWith(".zip") || fileName.endsWith(".csv")) {
                                            filez.delete();
                                        }
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show error message
                        progress.dismiss();
                        textView_SyncResidency.setText("Residency Download Error! Retry or Contact Administrator");
                        textView_SyncResidency.setTextColor(Color.RED);
                    }


                });


            }

        });


        //Sync Zipped Relationship
        final Button button_DownloadRelationship = findViewById(R.id.button_SyncRelationship);
        final TextView textView_SyncRelationship = findViewById(R.id.textView_SyncRelationship);
        button_DownloadRelationship.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progress.show();
                progress.setMessage("Updating Relationship...");
                Call<ResponseBody> call = dao.downloadRelationship();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            //progress.setMessage("Downloading zip file...");
                            progress.setMessage("Contacting Server...");
                            try {
                                // Read the response body into a file
                                progress.setMessage("Downloading Relationship Zip File...");
                                InputStream inputStream = response.body().byteStream();
                                File file = new File(getExternalCacheDir(), "relationship.zip");
                                if (file.exists()) {
                                    file.delete();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(response.body().bytes());
                                fileOutputStream.close();
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                // Unzip the file
                                progress.setMessage("Unzipping Relationship File...");
                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    String fileName = zipEntry.getName();
                                    File newFile = new File(getExternalCacheDir() + File.separator + fileName);
                                    FileOutputStream fos = new FileOutputStream(newFile);
                                    int len;
                                    while ((len = zipInputStream.read(buffer)) > 0) {
                                        fos.write(buffer, 0, len);
                                    }
                                    fos.close();
                                    zipEntry = zipInputStream.getNextEntry();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                relationshipDao = appDatabase.relationshipDao();
                                // Import the unzipped CSV file into the Room database
                                if (relationshipDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "relationship.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("rel_uuid").addColumn("aIsToB").addColumn("endDate").addColumn("endType")
                                            .addColumn("fw_uuid").addColumn("individual_uuid").addColumn("insertDate").addColumn("lcow").addColumn("man_uuid").addColumn("mar")
                                            .addColumn("mrank").addColumn("nchdm").addColumn("nwive").addColumn("polygamous")
                                            .addColumn("startDate").addColumn("tnbch").build();
                                    MappingIterator<Relationship> iterator = mapper.readerFor(Relationship.class).with(schema).readValues(unzippedFile);
                                    progress.show();
                                    AtomicInteger counts = new AtomicInteger();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<Relationship> relationships = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            Relationship relationship = iterator.next();
                                            if (relationship != null) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        progress.setMessage("Saving " + counts.incrementAndGet() + " of the Relationship");
                                                    }
                                                });
                                                relationships.add(relationship);
                                                batchCount++;
                                                if (batchCount == batchSize) {
                                                    relationshipDao.insert(relationships);
                                                    relationships.clear();
                                                    batchCount = 0;
                                                }
                                            }
                                        }
                                        if (batchCount > 0) {
                                            relationshipDao.insert(relationships);
                                        }
                                        progress.dismiss();
                                        textView_SyncRelationship.setText("Total Relationship Saved: " + counts);
                                        textView_SyncRelationship.setTextColor(Color.rgb(0, 114, 133));
                                    });
                                }

                                File cacheDir = getExternalCacheDir();
                                if (cacheDir != null && cacheDir.isDirectory()) {
                                    File[] files = cacheDir.listFiles();
                                    for (File filez : files) {
                                        String fileName = filez.getName();
                                        if (fileName.endsWith(".zip") || fileName.endsWith(".csv")) {
                                            filez.delete();
                                        }
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show error message
                        progress.dismiss();
                        textView_SyncRelationship.setText("Relationship Download Error! Retry or Contact Administrator");
                        textView_SyncRelationship.setTextColor(Color.RED);
                        //Toast.makeText(PullActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
                    }


                });


            }

        });



        //Sync Zipped Socialgroup
        final Button button_DownloadSocialgroup = findViewById(R.id.button_SyncSocialgroup);
        final TextView textView_SyncSocialgroup = findViewById(R.id.textView_SyncSocialgroup);
        button_DownloadSocialgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                progress.setMessage("Downloading Socialgroup...");
                Call<ResponseBody> call = dao.downloadSocialgroup();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            try {
                                // Read the response body into a file
                                progress.setMessage("Downloading Socialgroup Zip File...");
                                InputStream inputStream = response.body().byteStream();
                                File file = new File(getExternalCacheDir(), "socialgroup.zip");
                                if (file.exists()) {
                                    file.delete();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(response.body().bytes());
                                fileOutputStream.close();
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                // Unzip the file
                                progress.setMessage("Unzipping Socialgroup File...");
                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    String fileName = zipEntry.getName();
                                    File newFile = new File(getExternalCacheDir() + File.separator + fileName);
                                    FileOutputStream fos = new FileOutputStream(newFile);
                                    int len;
                                    while ((len = zipInputStream.read(buffer)) > 0) {
                                        fos.write(buffer, 0, len);
                                    }
                                    fos.close();
                                    zipEntry = zipInputStream.getNextEntry();

                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                socialgroupDao = appDatabase.socialgroupDao();
                                // Import the unzipped CSV file into the Room database
                                if (socialgroupDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "socialgroup.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("socialgroup_uuid").addColumn("houseExtId").addColumn("fw_uuid").addColumn("groupName").addColumn("groupType")
                                            .addColumn("individual_uuid").addColumn("insertDate").build();
                                    MappingIterator<Socialgroup> iterator = mapper.readerFor(Socialgroup.class).with(schema).readValues(unzippedFile);
                                    progress.show();
                                    AtomicInteger counts = new AtomicInteger();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<Socialgroup> socialgroups = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            Socialgroup socialgroup = iterator.next();
                                            if (socialgroup != null) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        progress.setMessage("Saving " + counts.incrementAndGet() + " of the Socialgroup");
                                                    }
                                                });
                                                socialgroups.add(socialgroup);
                                                batchCount++;
                                                if (batchCount == batchSize) {
                                                    socialgroupDao.insert(socialgroups);
                                                    socialgroups.clear();
                                                    batchCount = 0;
                                                }
                                            }
                                        }
                                        if (batchCount > 0) {
                                            socialgroupDao.insert(socialgroups);
                                        }
                                        progress.dismiss();
                                        textView_SyncSocialgroup.setText("Total Socialgroup Saved: " + counts);
                                        textView_SyncSocialgroup.setTextColor(Color.rgb(0, 114, 133));
                                    });
                                }

                                File cacheDir = getExternalCacheDir();
                                if (cacheDir != null && cacheDir.isDirectory()) {
                                    File[] files = cacheDir.listFiles();
                                    for (File filez : files) {
                                        String fileName = filez.getName();
                                        if (fileName.endsWith(".zip") || fileName.endsWith(".csv")) {
                                            filez.delete();
                                        }
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show error message
                        progress.dismiss();
                        textView_SyncSocialgroup.setText("Socialgroup Download Error! Retry or Contact Administrator");
                        textView_SyncSocialgroup.setTextColor(Color.RED);
                    }


                });


            }

        });


        //Sync Zipped Pregnancy
        final Button button_DownloadPregnancy = findViewById(R.id.button_SyncPregnancy);
        final TextView textView_SyncPregnancy = findViewById(R.id.textView_SyncPregnancy);
        button_DownloadPregnancy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progress.show();
                progress.setMessage("Downloading Pregnancy...");
                Call<ResponseBody> call = dao.downloadPregnancy();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            try {
                                // Read the response body into a file
                                InputStream inputStream = response.body().byteStream();
                                File file = new File(getExternalCacheDir(), "pregnancy.zip");
                                if (file.exists()) {
                                    file.delete();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(response.body().bytes());
                                fileOutputStream.close();
                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                // Unzip the file
                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    String fileName = zipEntry.getName();
                                    File newFile = new File(getExternalCacheDir() + File.separator + fileName);
                                    FileOutputStream fos = new FileOutputStream(newFile);
                                    int len;
                                    while ((len = zipInputStream.read(buffer)) > 0) {
                                        fos.write(buffer, 0, len);
                                    }
                                    fos.close();
                                    zipEntry = zipInputStream.getNextEntry();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                pregnancyDao = appDatabase.pregnancyDao();
                                // Import the unzipped CSV file into the Room database
                                if (pregnancyDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "pregnancy.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("obs_uuid").addColumn("ageOfPregFromPregNotes").addColumn("anc_visits").addColumn("anteNatalClinic").addColumn("attend_you")
                                            .addColumn("attend_you_other").addColumn("bnet_loc").addColumn("bnet_loc_other").addColumn("bnet_sou").addColumn("bnet_sou_other")
                                            .addColumn("estimatedAgeOfPreg").addColumn("expectedDeliveryDate").addColumn("first_preg").addColumn("first_rec")
                                            .addColumn("fw_uuid").addColumn("healthfacility").addColumn("how_many").addColumn("individual_uuid").addColumn("insertDate").addColumn("lastClinicVisitDate")
                                            .addColumn("medicineforpregnancy").addColumn("outcome").addColumn("outcome_date").addColumn("own_bnet")
                                            .addColumn("pregnancyNumber").addColumn("recordedDate").addColumn("slp_bednet").addColumn("trt_bednet").addColumn("ttinjection")
                                            .addColumn("visit_uuid").addColumn("why_no").addColumn("why_no_other").build();
                                    MappingIterator<Pregnancy> iterator = mapper.readerFor(Pregnancy.class).with(schema).readValues(unzippedFile);
                                    progress.show();
                                    AtomicInteger counts = new AtomicInteger();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<Pregnancy> pregnancies = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            Pregnancy pregnancy = iterator.next();
                                            if (pregnancy != null) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        progress.setMessage("Saving " + counts.incrementAndGet() + " of the Pregnancies");
                                                    }
                                                });
                                                pregnancies.add(pregnancy);
                                                batchCount++;
                                                if (batchCount == batchSize) {
                                                    pregnancyDao.insert(pregnancies);
                                                    pregnancies.clear();
                                                    batchCount = 0;
                                                }
                                            }
                                        }
                                        if (batchCount > 0) {
                                            pregnancyDao.insert(pregnancies);
                                        }
                                        progress.dismiss();
                                        textView_SyncPregnancy.setText("Total Pregnancy Saved: " + counts);
                                        textView_SyncPregnancy.setTextColor(Color.rgb(0, 114, 133));
                                    });
                                }

                                File cacheDir = getExternalCacheDir();
                                if (cacheDir != null && cacheDir.isDirectory()) {
                                    File[] files = cacheDir.listFiles();
                                    for (File filez : files) {
                                        String fileName = filez.getName();
                                        if (fileName.endsWith(".zip") || fileName.endsWith(".csv")) {
                                            filez.delete();
                                        }
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show error message
                        progress.dismiss();
                        textView_SyncPregnancy.setText("Pregnancy Download Error! Retry or Contact Administrator");
                        textView_SyncPregnancy.setTextColor(Color.RED);
                    }


                });


            }

        });


        //Sync Zipped Demography
        final Button button_DownloadDemography = findViewById(R.id.button_SyncDemography);
        final TextView textView_SyncDemography = findViewById(R.id.textView_SyncDemography);
        button_DownloadDemography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                progress.setMessage("Downloading Demographics...");
                Call<ResponseBody> call = dao.downloadDemography();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            progress.setMessage("Contacting Server...");
                            try {
                                // Read the response body into a file
                                progress.setMessage("Downloading Demograpics Zip File...");
                                InputStream inputStream = response.body().byteStream();
                                File file = new File(getExternalCacheDir(), "demography.zip");
                                if (file.exists()) {
                                    file.delete();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(response.body().bytes());
                                fileOutputStream.close();

                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                // Unzip the file
                                progress.setMessage("Unzipping Demograpics File...");
                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    String fileName = zipEntry.getName();
                                    File newFile = new File(getExternalCacheDir() + File.separator + fileName);
                                    FileOutputStream fos = new FileOutputStream(newFile);
                                    int len;
                                    while ((len = zipInputStream.read(buffer)) > 0) {
                                        fos.write(buffer, 0, len);
                                    }
                                    fos.close();
                                    zipEntry = zipInputStream.getNextEntry();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                demographicDao = appDatabase.demographicDao();
                                // Import the unzipped CSV file into the Room database
                                if (demographicDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "demographics.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("individual_uuid").addColumn("comp_yrs").addColumn("education").addColumn("fw_uuid")
                                            .addColumn("insertDate").addColumn("marital").addColumn("occupation").addColumn("occupation_oth")
                                            .addColumn("phone1").addColumn("phone2")
                                            .addColumn("religion").addColumn("religion_oth").addColumn("tribe").addColumn("tribe_oth").build();
                                    MappingIterator<Demographic> iterator = mapper.readerFor(Demographic.class).with(schema).readValues(unzippedFile);
                                    progress.show();
                                    AtomicInteger counts = new AtomicInteger();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<Demographic> demographics = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            Demographic demographic = iterator.next();
                                            if (demographic != null) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        progress.setMessage("Saving " + counts.incrementAndGet() + " of the demographics");
                                                    }
                                                });
                                                demographics.add(demographic);
                                                batchCount++;
                                                if (batchCount == batchSize) {
                                                    demographicDao.insert(demographics);
                                                    demographics.clear();
                                                    batchCount = 0;
                                                }
                                            }
                                        }
                                        if (batchCount > 0) {
                                            demographicDao.insert(demographics);
                                        }
                                        progress.dismiss();
                                        textView_SyncDemography.setText("Total Demographics Saved: " + counts);
                                        textView_SyncDemography.setTextColor(Color.rgb(0, 114, 133));
                                    });
                                }

                                File cacheDir = getExternalCacheDir();
                                if (cacheDir != null && cacheDir.isDirectory()) {
                                    File[] files = cacheDir.listFiles();
                                    for (File filez : files) {
                                        String fileName = filez.getName();
                                        if (fileName.endsWith(".zip") || fileName.endsWith(".csv")) {
                                            filez.delete();
                                        }
                                    }
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show error message
                        progress.dismiss();
                        textView_SyncDemography.setText("Demography Download Error! Retry or Contact Administrator");
                        textView_SyncDemography.setTextColor(Color.RED);
                    }


                });


            }

        });


        //Sync Zipped SES
        final Button button_DownloadSes = findViewById(R.id.button_SyncSes);
        final TextView textView_SyncSes = findViewById(R.id.textView_SyncSes);
        button_DownloadSes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                progress.setMessage("Downloading SES...");
                Call<ResponseBody> call = dao.downloadSes();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            progress.setMessage("Contacting Server...");
                            try {
                                // Read the response body into a file
                                progress.setMessage("Downloading SES Zip File...");
                                InputStream inputStream = response.body().byteStream();
                                File file = new File(getExternalCacheDir(), "ses.zip");
                                if (file.exists()) {
                                    file.delete();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(response.body().bytes());
                                fileOutputStream.close();

                                byte[] buffer = new byte[1024];
                                int read;
                                while ((read = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, read);
                                }
                                fileOutputStream.close();

                                // Unzip the file
                                progress.setMessage("Unzipping SES File...");
                                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                                ZipEntry zipEntry = zipInputStream.getNextEntry();
                                while (zipEntry != null) {
                                    String fileName = zipEntry.getName();
                                    File newFile = new File(getExternalCacheDir() + File.separator + fileName);
                                    FileOutputStream fos = new FileOutputStream(newFile);
                                    int len;
                                    while ((len = zipInputStream.read(buffer)) > 0) {
                                        fos.write(buffer, 0, len);
                                    }
                                    fos.close();
                                    zipEntry = zipInputStream.getNextEntry();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                hdssSociodemoDao = appDatabase.hdssSociodemoDao();
                                // Import the unzipped CSV file into the Room database
                                if (hdssSociodemoDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "ses.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("socialgroup_uuid")
                                            .addColumn("aircon_fcorres").addColumn("aircon_num_fcorres").addColumn("animal_othr_fcorres").addColumn("animal_othr_num_fcorres")
                                            .addColumn("animal_othr_spfy_fcorres").addColumn("bike_fcorres").addColumn("bike_num_fcorres").addColumn("blender_fcorres")
                                            .addColumn("blender_num_fcorres").addColumn("boat_fcorres").addColumn("boat_num_fcorres").addColumn("cabinets_fcorres")
                                            .addColumn("cabinets_num_fcorres").addColumn("car_fcorres").addColumn("car_num_fcorres").addColumn("cart_fcorres")
                                            .addColumn("cart_num_fcorres").addColumn("cattle_fcorres").addColumn("cattle_num_fcorres").addColumn("cethnic")
                                            .addColumn("chew_bnut_oecoccur").addColumn("chew_oecoccur").addColumn("computer_fcorres").addColumn("computer_num_fcorres")
                                            .addColumn("cooking_inside_fcorres").addColumn("cooking_loc_fcorres").addColumn("cooking_room_fcorres").addColumn("cooking_vent_fcorres")
                                            .addColumn("donkey_fcorres").addColumn("donkey_num_fcorres").addColumn("drink_oecoccur").addColumn("dvd_cd_fcorres")
                                            .addColumn("dvd_cd_num_fcorres").addColumn("electricity_fcorres").addColumn("ext_wall_fcorres").addColumn("ext_wall_spfy_fcorres")
                                            .addColumn("floor_fcorres").addColumn("floor_spfy_fcorres").addColumn("foam_matt_fcorres").addColumn("foam_matt_num_fcorres")
                                            .addColumn("form_comments_txt").addColumn("form_comments_yn").addColumn("fridge_fcorres").addColumn("fridge_num_fcorres")
                                            .addColumn("fw_uuid").addColumn("goat_fcorres").addColumn("goat_num_fcorres")
                                            .addColumn("h2o_dist_fcorres").addColumn("h2o_fcorres").addColumn("h2o_hours_fcorres")
                                            .addColumn("h2o_mins_fcorres").addColumn("h2o_prep_fcorres").addColumn("h2o_prep_spfy_fcorres_1")
                                            .addColumn("h2o_prep_spfy_fcorres_2").addColumn("h2o_prep_spfy_fcorres_3")
                                            .addColumn("h2o_prep_spfy_fcorres_4").addColumn("h2o_prep_spfy_fcorres_5").addColumn("h2o_spfy_fcorres")
                                            .addColumn("head_hh_fcorres").addColumn("head_hh_spfy_fcorres").addColumn("horse_fcorres")
                                            .addColumn("horse_num_fcorres").addColumn("house_occ_ge5_fcorres").addColumn("house_occ_lt5_fcorres")
                                            .addColumn("house_occ_tot_fcorres").addColumn("house_room_child_fcorres")
                                            .addColumn("house_rooms_fcorres").addColumn("individual_uuid").addColumn("insertDate")
                                            .addColumn("internet_fcorres").addColumn("job_busown_spfy_scorres").addColumn("job_othr_spfy_scorres")
                                            .addColumn("job_salary_spfy_scorres").addColumn("job_scorres").addColumn("job_skilled_spfy_scorres")
                                            .addColumn("job_smbus_spfy_scorres").addColumn("job_unskilled_spfy_scorres").addColumn("land_fcorres")
                                            .addColumn("land_use_fcorres_1").addColumn("land_use_fcorres_2").addColumn("land_use_fcorres_3")
                                            .addColumn("land_use_fcorres_4").addColumn("land_use_fcorres_5").addColumn("land_use_fcorres_88")
                                            .addColumn("land_use_spfy_fcorres_88").addColumn("landline_fcorres").addColumn("lantern_fcorres")
                                            .addColumn("lantern_num_fcorres").addColumn("livestock_fcorres").addColumn("location_uuid")
                                            .addColumn("marital_age").addColumn("marital_scorres").addColumn("mnh03_formcompldat")
                                            .addColumn("mobile_access_fcorres").addColumn("mobile_fcorres").addColumn("mobile_num_fcorres")
                                            .addColumn("mosquito_net_fcorres").addColumn("mosquito_net_num_fcorres").addColumn("motorcycle_fcorres")
                                            .addColumn("motorcycle_num_fcorres").addColumn("nth_trb_spfy_cethnic")
                                            .addColumn("othr_trb_spfy_cethnic").addColumn("own_rent_scorres").addColumn("own_rent_spfy_scorres")
                                            .addColumn("pd_birth_fac_spfy_oholoc").addColumn("pd_birth_oholoc")
                                            .addColumn("pd_birth_othr_spfy_oholoc").addColumn("pd_dm_rel_spfy_scorres").addColumn("pd_dm_scorres")
                                            .addColumn("pig_fcorres").addColumn("pig_num_fcorres").addColumn("plough_fcorres")
                                            .addColumn("plough_num_fcorres").addColumn("poultry_fcorres").addColumn("poultry_num_fcorres")
                                            .addColumn("ptr_busown_spfy_scorres").addColumn("ptr_othr_spfy_scorres")
                                            .addColumn("ptr_salary_spfy_scorres").addColumn("ptr_scorres").addColumn("ptr_skilled_spfy_scorres")
                                            .addColumn("ptr_smbus_spfy_scorres").addColumn("ptr_unskilled_spfy_scorres").addColumn("radio_fcorres")
                                            .addColumn("radio_num_fcorres").addColumn("religion_scorres").addColumn("religion_spfy_scorres")
                                            .addColumn("roof_fcorres").addColumn("roof_spfy_fcorres").addColumn("sat_dish_fcorres")
                                            .addColumn("sat_dish_num_fcorres").addColumn("sd_obsstdat").addColumn("sew_fcorres")
                                            .addColumn("sew_num_fcorres").addColumn("sheep_fcorres").addColumn("sheep_num_fcorres")
                                            .addColumn("smoke_hhold_in_oecdosfrq").addColumn("smoke_hhold_oecoccur").addColumn("smoke_in_oecdosfrq")
                                            .addColumn("smoke_oecoccur").addColumn("sofa_fcorres").addColumn("sofa_num_fcorres")
                                            .addColumn("solar_fcorres").addColumn("spring_matt_fcorres").addColumn("spring_matt_num_fcorres")
                                            .addColumn("stove_fcorres").addColumn("stove_fuel_fcorres_1").addColumn("stove_fuel_fcorres_10")
                                            .addColumn("stove_fuel_fcorres_11").addColumn("stove_fuel_fcorres_12")
                                            .addColumn("stove_fuel_fcorres_13").addColumn("stove_fuel_fcorres_14").addColumn("stove_fuel_fcorres_2")
                                            .addColumn("stove_fuel_fcorres_3").addColumn("stove_fuel_fcorres_4").addColumn("stove_fuel_fcorres_5")
                                            .addColumn("stove_fuel_fcorres_6").addColumn("stove_fuel_fcorres_7").addColumn("stove_fuel_fcorres_8")
                                            .addColumn("stove_fuel_fcorres_88").addColumn("stove_fuel_fcorres_9")
                                            .addColumn("stove_fuel_spfy_fcorres_88").addColumn("stove_spfy_fcorres").addColumn("straw_matt_fcorres")
                                            .addColumn("straw_matt_num_fcorres").addColumn("tables_fcorres").addColumn("tables_num_fcorres")
                                            .addColumn("toilet_fcorres").addColumn("toilet_loc_fcorres").addColumn("toilet_loc_spfy_fcorres")
                                            .addColumn("toilet_share_fcorres").addColumn("toilet_share_num_fcorres")
                                            .addColumn("toilet_spfy_fcorres").addColumn("tractor_fcorres").addColumn("tractor_num_fcorres")
                                            .addColumn("tricycles_fcorres").addColumn("tricycles_num_fcorres").addColumn("tv_fcorres")
                                            .addColumn("tv_num_fcorres").addColumn("wash_fcorres").addColumn("wash_num_fcorres")
                                            .addColumn("watch_fcorres")
                                            .addColumn("watch_num_fcorres").build();
                                    MappingIterator<HdssSociodemo> iterator = mapper.readerFor(HdssSociodemo.class).with(schema).readValues(unzippedFile);
                                    progress.show();
                                    AtomicInteger counts = new AtomicInteger();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<HdssSociodemo> hdssSociodemos = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            HdssSociodemo hdssSociodemo = iterator.next();
                                            if (hdssSociodemo != null) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        progress.setMessage("Saving " + counts.incrementAndGet() + " of the SES");
                                                    }
                                                });
                                                hdssSociodemos.add(hdssSociodemo);
                                                batchCount++;
                                                if (batchCount == batchSize) {
                                                    hdssSociodemoDao.insert(hdssSociodemos);
                                                    hdssSociodemos.clear();
                                                    batchCount = 0;
                                                }
                                            }
                                        }
                                        if (batchCount > 0) {
                                            hdssSociodemoDao.insert(hdssSociodemos);
                                        }
                                        progress.dismiss();
                                        textView_SyncSes.setText("Total SES Saved: " + counts);
                                        textView_SyncSes.setTextColor(Color.rgb(0, 114, 133));
                                    });
                                }

                                File cacheDir = getExternalCacheDir();
                                if (cacheDir != null && cacheDir.isDirectory()) {
                                    File[] files = cacheDir.listFiles();
                                    for (File filez : files) {
                                        String fileName = filez.getName();
                                        if (fileName.endsWith(".zip") || fileName.endsWith(".csv")) {
                                            filez.delete();
                                        }
                                    }
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Show error message
                        progress.dismiss();
                        textView_SyncSes.setText("SES Download Error! Retry or Contact Administrator");
                        textView_SyncSes.setTextColor(Color.RED);
                    }


                });


            }

        });


    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit_confirmation_title))
                .setMessage(getString(R.string.exiting_lbl))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            PullActivity.this.finish();
                        }
                        catch(Exception e){}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }


}