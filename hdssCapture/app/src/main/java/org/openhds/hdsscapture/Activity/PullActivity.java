package org.openhds.hdsscapture.Activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import org.openhds.hdsscapture.Viewmodel.ClusterViewModel;
import org.openhds.hdsscapture.Viewmodel.CodeBookViewModel;
import org.openhds.hdsscapture.Viewmodel.CountryViewModel;
import org.openhds.hdsscapture.Viewmodel.DistrictViewModel;
import org.openhds.hdsscapture.Viewmodel.RegionViewModel;
import org.openhds.hdsscapture.Viewmodel.RoundViewModel;
import org.openhds.hdsscapture.Viewmodel.SubdistrictViewModel;
import org.openhds.hdsscapture.Viewmodel.VillageViewModel;
import org.openhds.hdsscapture.entity.Cluster;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.Country;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.District;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Region;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Round;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Subdistrict;
import org.openhds.hdsscapture.entity.Village;
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


        AppJson api = AppJson.getInstance();
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

            progress.setMessage("Updating Countries...");

            final CountryViewModel location = new ViewModelProvider(PullActivity.this).get(CountryViewModel.class);

            Call<DataWrapper<Country>> c_callable = dao.getAllCountries();
            c_callable.enqueue(new Callback<DataWrapper<Country>>() {
                @Override
                public void onResponse(Call<DataWrapper<Country>> call, Response<DataWrapper<Country>> response) {
                    Country[] countries = response.body().getData().toArray(new Country[0]);
                    location.add(countries);

                    //when country is synched, synch region
                    progress.setMessage("Updating Regions...");
                    final RegionViewModel location = new ViewModelProvider(PullActivity.this).get(RegionViewModel.class);
                    Call<DataWrapper<Region>> c_callable = dao.getAllRegions();
                    c_callable.enqueue(new Callback<DataWrapper<Region>>() {
                        @Override
                        public void onResponse(Call<DataWrapper<Region>> call, Response<DataWrapper<Region>> response) {
                            Region[] regions = response.body().getData().toArray(new Region[0]);
                            location.add(regions);

                            //when region is synched, sync district
                            progress.setMessage("Updating Districts...");
                            final DistrictViewModel districtViewModel = new ViewModelProvider(PullActivity.this).get(DistrictViewModel.class);
                            Call<DataWrapper<District>> c_callable = dao.getAllDistricts();
                            c_callable.enqueue(new Callback<DataWrapper<District>>() {
                                @Override
                                public void onResponse(Call<DataWrapper<District>> call, Response<DataWrapper<District>> response) {
                                    District[] districts = response.body().getData().toArray(new District[0]);
                                    districtViewModel.add(districts);

                                    //when District is synched, sync subdistrict
                                    progress.setMessage("Updating Subdistricts...");
                                    final SubdistrictViewModel subdistrictViewModel = new ViewModelProvider(PullActivity.this).get(SubdistrictViewModel.class);
                                    Call<DataWrapper<Subdistrict>> c_callable = dao.getAllSubDistricts();
                                    c_callable.enqueue(new Callback<DataWrapper<Subdistrict>>() {
                                        @Override
                                        public void onResponse(Call<DataWrapper<Subdistrict>> call, Response<DataWrapper<Subdistrict>> response) {
                                            Subdistrict[] subdistricts = response.body().getData().toArray(new Subdistrict[0]);
                                            subdistrictViewModel.add(subdistricts);

                                            //when district is done sync villages
                                            progress.setMessage("Updating Villages...");
                                            final VillageViewModel location = new ViewModelProvider(PullActivity.this).get(VillageViewModel.class);
                                            Call<DataWrapper<Village>> c_callable = dao.getAllVillages();
                                            c_callable.enqueue(new Callback<DataWrapper<Village>>() {
                                                @Override
                                                public void onResponse(Call<DataWrapper<Village>> call, Response<DataWrapper<Village>> response) {
                                                    Village[] villages = response.body().getData().toArray(new Village[0]);
                                                    location.add(villages);

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

                                                            //when villages is done, sync clusters
                                                            progress.setMessage("Updating Clusters...");
                                                            Call<DataWrapper<Cluster>> c_callable = dao.getAllClusters();
                                                            final ClusterViewModel location = new ViewModelProvider(PullActivity.this).get(ClusterViewModel.class);
                                                            c_callable.enqueue(new Callback<DataWrapper<Cluster>>() {
                                                                @Override
                                                                public void onResponse(Call<DataWrapper<Cluster>> call, Response<DataWrapper<Cluster>> response) {
                                                                    Cluster[] clusters = response.body().getData().toArray(new Cluster[0]);
                                                                    location.add(clusters);

                                                                    progress.dismiss();
                                                                    textView_SyncHierarchyData.setText("" + villages.length + " Villages & " + clusters.length + " Clusters");
                                                                    textView_SyncHierarchyData.setTextColor(Color.rgb(0, 114, 133));
                                                                }

                                                                @Override
                                                                public void onFailure(Call<DataWrapper<Cluster>> call, Throwable t) {
                                                                    progress.dismiss();
                                                                    textView_SyncHierarchyData.setText("Sync Error!");
                                                                    textView_SyncHierarchyData.setTextColor(Color.RED);
                                                                }
                                                            });
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
                                                            textView_SyncHierarchyData.setText("Sync Error!");
                                                            textView_SyncHierarchyData.setTextColor(Color.RED);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(Call<DataWrapper<Village>> call, Throwable t) {
                                                    progress.dismiss();
                                                    textView_SyncHierarchyData.setText("Sync Error!");
                                                    textView_SyncHierarchyData.setTextColor(Color.RED);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(Call<DataWrapper<Subdistrict>> call, Throwable t) {
                                            progress.dismiss();
                                            textView_SyncHierarchyData.setText("Sync Error!");
                                            textView_SyncHierarchyData.setTextColor(Color.RED);
                                        }
                                    });
                                }


                                @Override
                                public void onFailure(Call<DataWrapper<District>> call, Throwable t) {
                                    progress.dismiss();
                                    textView_SyncHierarchyData.setText("Sync Error!");
                                    textView_SyncHierarchyData.setTextColor(Color.RED);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<DataWrapper<Region>> call, Throwable t) {
                            progress.dismiss();
                            textView_SyncHierarchyData.setText("Sync Error!");
                            textView_SyncHierarchyData.setTextColor(Color.RED);
                        }
                    });
                }

                @Override
                public void onFailure(Call<DataWrapper<Country>> call, Throwable t) {
                    progress.dismiss();
                    textView_SyncHierarchyData.setText("Sync Error!");
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
                Call<ResponseBody> call = dao.downloadZipFile();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            //progress.setMessage("Downloading zip file...");
                            progress.setMessage("Contacting Server...");
                            progress.dismiss();
                            Toast.makeText(PullActivity.this, "Server success", Toast.LENGTH_SHORT).show();
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
                                progress.dismiss();
                                Toast.makeText(PullActivity.this, "Download success", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(PullActivity.this, "Successfully Unzipped", Toast.LENGTH_SHORT).show();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                individualDao = appDatabase.individualDao();
                                // Import the unzipped CSV file into the Room database
                                if (individualDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "individual.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("extId").addColumn("dob").addColumn("dobAspect").addColumn("father")
                                            .addColumn("firstName").addColumn("fw").addColumn("gender").addColumn("insertDate")
                                            .addColumn("lastName").addColumn("mother").addColumn("nickName").build();
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
                                                        progress.setMessage("Processing " + counts.incrementAndGet() + " of the Individuals");
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
                        Toast.makeText(PullActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
                    }


                });


            }

        });


        //Sync Zipped Location
        final Button button_DownloadLocation = findViewById(R.id.button_SyncLocationData);
        final TextView textView_SyncLocation = findViewById(R.id.textView_SyncLocationData);
        button_DownloadLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = dao.downloadLocation();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            Toast.makeText(PullActivity.this, "Server success", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PullActivity.this, "Download success", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(PullActivity.this, "Successfully Unzipped", Toast.LENGTH_SHORT).show();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                locationDao = appDatabase.locationDao();
                                // Import the unzipped CSV file into the Room database
                                if (locationDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "location.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("extId").addColumn("clusterId").addColumn("locationType").addColumn("accuracy")
                                            .addColumn("compno").addColumn("fw").addColumn("insertDate").addColumn("latitude")
                                            .addColumn("locationName").addColumn("longitude").addColumn("status").build();
                                    MappingIterator<Location> iterator = mapper.readerFor(Location.class).with(schema).readValues(unzippedFile);
                                    progress.setCancelable(true);
                                    progress.setCanceledOnTouchOutside(true);
                                    progress.show();
                                    AtomicInteger counts = new AtomicInteger();
                                    AppDatabase.databaseWriteExecutor.execute(() -> {
                                        int batchSize = 10000;
                                        List<Location> locations = new ArrayList<>();
                                        int batchCount = 0;
                                        while (iterator.hasNext()) {
                                            Location location = iterator.next();
                                            if (location != null) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        progress.setMessage("Processing " + counts.incrementAndGet() + " of the Compounds");
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
                        Toast.makeText(PullActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
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
                Call<ResponseBody> call = dao.downloadResidency();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PullActivity.this, "Server success", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PullActivity.this, "Download success", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(PullActivity.this, "Successfully Unzipped Residency", Toast.LENGTH_SHORT).show();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                residencyDao = appDatabase.residencyDao();
                                // Import the unzipped CSV file into the Room database
                                if (residencyDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "residency.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("uuid").addColumn("compno").addColumn("endDate").addColumn("endType")
                                            .addColumn("extId").addColumn("fw").addColumn("insertDate").addColumn("location")
                                            .addColumn("socialgroup").addColumn("startDate").addColumn("startType").build();
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
                                                        progress.setMessage("Processing " + counts.incrementAndGet() + " of the Membership");
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
                        Toast.makeText(PullActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
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
                Call<ResponseBody> call = dao.downloadRelationship();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            //progress.setMessage("Downloading zip file...");
                            progress.setMessage("Contacting Server...");
                            progress.dismiss();
                            Toast.makeText(PullActivity.this, "Server success", Toast.LENGTH_SHORT).show();
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
                                progress.dismiss();
                                Toast.makeText(PullActivity.this, "Download success", Toast.LENGTH_SHORT).show();
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
                                    progress.dismiss();
                                    Toast.makeText(PullActivity.this, "Successfully Unzipped Relationship", Toast.LENGTH_SHORT).show();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                relationshipDao = appDatabase.relationshipDao();
                                // Import the unzipped CSV file into the Room database
                                if (relationshipDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "relationship.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("uuid").addColumn("aIsToB").addColumn("endDate").addColumn("endType")
                                            .addColumn("extId").addColumn("extIdB").addColumn("fw").addColumn("insertDate")
                                            .addColumn("startDate").build();
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
                                                        progress.setMessage("Processing " + counts.incrementAndGet() + " of the Relationship");
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
                        Toast.makeText(PullActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
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
                Call<ResponseBody> call = dao.downloadSocialgroup();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            Toast.makeText(PullActivity.this, "Server success", Toast.LENGTH_SHORT).show();
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
                                progress.dismiss();
                                Toast.makeText(PullActivity.this, "Download success", Toast.LENGTH_SHORT).show();
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
                                    progress.dismiss();
                                    Toast.makeText(PullActivity.this, "Successfully Unzipped Socialgroup", Toast.LENGTH_SHORT).show();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                socialgroupDao = appDatabase.socialgroupDao();
                                // Import the unzipped CSV file into the Room database
                                if (socialgroupDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "socialgroup.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("extId").addColumn("fw").addColumn("groupName").addColumn("groupType")
                                            .addColumn("headid").addColumn("insertDate").build();
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
                                                        progress.setMessage("Processing " + counts.incrementAndGet() + " of the Socialgroup");
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
                        Toast.makeText(PullActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
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
                Call<ResponseBody> call = dao.downloadPregnancy();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            Toast.makeText(PullActivity.this, "Server success", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PullActivity.this, "Download success", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(PullActivity.this, "Successfully Unzipped Pregnancy", Toast.LENGTH_SHORT).show();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                pregnancyDao = appDatabase.pregnancyDao();
                                // Import the unzipped CSV file into the Room database
                                if (pregnancyDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "pregnancy.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("uuid").addColumn("extId").addColumn("expectedDeliveryDate").addColumn("fw")
                                            .addColumn("insertDate").addColumn("outcome").addColumn("recordedDate").addColumn("visitid").build();
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
                                                        progress.setMessage("Processing " + counts.incrementAndGet() + " of the Pregnancies");
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
                        Toast.makeText(PullActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
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
                Call<ResponseBody> call = dao.downloadDemography();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //progress.show();
                        if (response.isSuccessful()) {
                            //progress.setMessage("Downloading zip file...");
                            progress.setMessage("Contacting Server...");
                            progress.dismiss();
                            Toast.makeText(PullActivity.this, "Server success", Toast.LENGTH_SHORT).show();
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
                                progress.dismiss();
                                Toast.makeText(PullActivity.this, "Download success", Toast.LENGTH_SHORT).show();
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
                                    progress.dismiss();
                                    Toast.makeText(PullActivity.this, "Successfully Unzipped Demography", Toast.LENGTH_SHORT).show();
                                }
                                zipInputStream.close();

                                AppDatabase appDatabase = AppDatabase.getDatabase(PullActivity.this);
                                demographicDao = appDatabase.demographicDao();
                                // Import the unzipped CSV file into the Room database
                                if (demographicDao != null) {
                                    File unzippedFile = new File(getExternalCacheDir() + File.separator + "demography.csv");
                                    CsvMapper mapper = new CsvMapper();
                                    CsvSchema schema = CsvSchema.builder().addColumn("extId").addColumn("education").addColumn("fw").addColumn("insertDate")
                                            .addColumn("marital").addColumn("occupation").addColumn("phone1").addColumn("phone2")
                                            .addColumn("religion").addColumn("tribe").build();
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
                                                        progress.setMessage("Processing " + counts.incrementAndGet() + " of the demographics");
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
                        Toast.makeText(PullActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
                    }


                });


            }

        });


    }


}