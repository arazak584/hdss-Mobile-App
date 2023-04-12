package org.openhds.hdsscapture.Activity;

import android.app.ProgressDialog;
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
                                            .addColumn("father_uuid").addColumn("firstName").addColumn("fw_uuid").addColumn("gender").addColumn("insertDate")
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
                progress.setMessage("Downloading Demography...");
                Call<ResponseBody> call = dao.downloadDemography();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            progress.setMessage("Contacting Server...");
                            try {
                                // Read the response body into a file
                                progress.setMessage("Downloading Demography Zip File...");
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
                                progress.setMessage("Unzipping Demography File...");
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
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "demography.csv");
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
                                        textView_SyncDemography.setText("Total Demography Saved: " + counts);
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


    }


}