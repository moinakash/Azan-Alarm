package com.example.azan;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.azan.fragments.InitialConfigFragment;
import com.example.azan.fragments.KaabaLocatorFragment;
import com.example.azan.fragments.LocationHelper;
import com.example.azan.fragments.SalaatTimesFragment;
import com.example.azan.util.AppSettings;
import com.example.azan.util.PermissionUtil;
import com.example.azan.util.ScreenUtils;
import com.example.azan.widget.FragmentStatePagerAdapter;
import com.example.azan.widget.SlidingTabLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.util.Calendar;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.N)
public class SalaatTimesActivity extends AppCompatActivity implements Constants,
    InitialConfigFragment.OnOptionSelectedListener, ViewPager.OnPageChangeListener,
    LocationHelper.LocationCallback {

  private LocationHelper mLocationHelper;
  private Location mLastLocation = null;

  private ViewPager mPager;
  private ScreenSlidePagerAdapter mAdapter;
  private SlidingTabLayout mTabs;

  private TextView BTV,ATV;
  public String text;
  int sum = 0;
  int mm = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppSettings settings = AppSettings.getInstance(this);
    //INIT APP
    if (!settings.getBoolean(AppSettings.Key.IS_INIT)) {
      settings.set(settings.getKeyFor(AppSettings.Key.IS_ALARM_SET,         0), true);
      settings.set(settings.getKeyFor(AppSettings.Key.IS_FAJR_ALARM_SET,    0), true);
      settings.set(settings.getKeyFor(AppSettings.Key.IS_DHUHR_ALARM_SET,   0), true);
      settings.set(settings.getKeyFor(AppSettings.Key.IS_ASR_ALARM_SET,     0), true);
      settings.set(settings.getKeyFor(AppSettings.Key.IS_MAGHRIB_ALARM_SET, 0), true);
      settings.set(settings.getKeyFor(AppSettings.Key.IS_ISHA_ALARM_SET,    0), true);
      settings.set(AppSettings.Key.USE_ADHAN, true);
      settings.set(AppSettings.Key.IS_INIT, true);
    }

    setContentView(R.layout.activity_salaat_times);
    ScreenUtils.lockOrientation(this);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    date();
    mLocationHelper = (LocationHelper) getFragmentManager().findFragmentByTag(LOCATION_FRAGMENT);

    // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
    mAdapter = new ScreenSlidePagerAdapter(getFragmentManager(),0);

    // Assigning ViewPager View and setting the adapter
    mPager = (ViewPager) findViewById(R.id.pager);
    mPager.setAdapter(mAdapter);
    mPager.addOnPageChangeListener(this);

    // Assiging the Sliding Tab Layout View
    mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
    mTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

    // Setting Custom Color for the Scroll bar indicator of the Tab View
/*
    mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
      @Override
      public int getIndicatorColor(int position) {
        return getResources().getColor(R.color.teal_accent);
      }
    });
*/
    mTabs.setSelectedIndicatorColors(getResources().getColor(android.R.color.primary_text_dark));
    mTabs.setTextColor(android.R.color.primary_text_dark);

    // Setting the ViewPager For the SlidingTabsLayout
    mTabs.setViewPager(mPager);

    if(mLocationHelper == null) {
      mLocationHelper = LocationHelper.newInstance();
      getFragmentManager().beginTransaction().add(mLocationHelper, LOCATION_FRAGMENT).commit();
    }

//    if (!settings.getBoolean(AppSettings.Key.IS_TNC_ACCEPTED, false)) {
//      getWindow().getDecorView().postDelayed(new Runnable() {
//        @Override
//        public void run() {
//          Intent intent = new Intent(SalaatTimesActivity.this, TermsAndConditionsActivity.class);
//          overridePendingTransition(R.anim.enter_from_bottom, R.anim.no_animation);
//          startActivityForResult(intent, REQUEST_TNC);
//        }
//      }, 2000);
//    }



  }


  private void date() {


    BTV = (TextView) findViewById(R.id.bangladate);
    ATV = (TextView) findViewById(R.id.arbidate);

    Locale locale = new Locale("bn");
    Locale.setDefault(locale);
    Configuration config =
            getBaseContext().getResources().getConfiguration();
    config.setLocale(locale);

    createConfigurationContext(config);

    Calendar cl = Calendar.getInstance(locale);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy ");

    HijrahDate islamyDate = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      islamyDate = HijrahChronology.INSTANCE.date(LocalDate.of(cl.get(Calendar.YEAR), cl.get(Calendar.MONTH) + 1, cl.get(Calendar.DATE)));
      text = islamyDate.toString();
      text = text.replace("Hijrah-umalqura AH", "");

      ////////////////////////////////////////////////////////////////////////////////////

      ///////////////////////////////////////////////////////////////////////////


      text = text.replace("0", "০");
      text = text.replace("1", "১");
      text = text.replace("2", "২");
      text = text.replace("3", "৩");
      text = text.replace("4", "৪");
      text = text.replace("5", "৫");
      text = text.replace("6", "৬");
      text = text.replace("7", "৭");
      text = text.replace("8", "৮");
      text = text.replace("9", "৯");


      //////////////////////////////////////////////////////

      text = text.replace("-০১-", ",মহরম,");
      text = text.replace("-০2-", "সফল");
      text = text.replace("-০৩-", "রবিউল আউয়াল");
      text = text.replace("-০৪-", "রবিউস সানি");
      text = text.replace("-০৫-", "জমাদিউল আউয়াল");
      text = text.replace("-০৬-", "জমাদিউস সানি");
      text = text.replace("-০৭-", "রজব");
      text = text.replace("-০৮-", "শাবান");
      text = text.replace("-০৯-", "রমজান");
      text = text.replace("-১০-", "শওয়াল");
      text = text.replace("-১১-", ",জিলক্বদ,");
      text = text.replace("-১২-", "জিলহজ্জ");
      ATV.setText(text+" হিঃ");

    }

//    }
//    if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.O) {
//      text = "Its only for android orio";
//
//    }

    ATV.setText(text+" হিঃ");

  }

  @Override
  protected void onResume() {
    super.onResume();

    if (mLastLocation == null) {
      fetchLocation();
    }
  }

  @Override
  protected void onDestroy() {
    //Just to be sure memory is cleaned up.
    mPager.removeOnPageChangeListener(this);
    mPager = null;
    mAdapter = null;
    mTabs = null;
    mLastLocation = null;

    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_salaat_times, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
//    if (id == R.id.action_settings) {
//      startOnboardingFor(0);
//      return true;
//    } else if (id == R.id.action_terms) {
//      Intent intent = new Intent(SalaatTimesActivity.this, TermsAndConditionsActivity.class);
//      intent.putExtra(TermsAndConditionsActivity.EXTRA_DISPLAY_ONLY, true);
//      overridePendingTransition(R.anim.enter_from_bottom, R.anim.no_animation);
//      startActivityForResult(intent, REQUEST_TNC);
//    }

    return super.onOptionsItemSelected(item);
  }

  private void startOnboardingFor(int index) {
    Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
    intent.putExtra(OnboardingActivity.EXTRA_CARD_INDEX, index);
    startActivityForResult(intent, REQUEST_ONBOARDING);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CHECK_SETTINGS) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          // All required changes were successfully made
          fetchLocation();
          break;
        case Activity.RESULT_CANCELED:
          // The user was asked to change settings, but chose not to
          onLocationSettingsFailed();
          break;
        default:
          onLocationSettingsFailed();
          break;
      }
    } else if (requestCode == REQUEST_ONBOARDING) {
      if (resultCode == RESULT_OK) {
        onUseDefaultSelected();
      }
    } else if (requestCode == REQUEST_TNC) {
      if (resultCode == RESULT_CANCELED) {
        finish();
      } else {
        AppSettings settings = AppSettings.getInstance(this);
        settings.set(AppSettings.Key.IS_TNC_ACCEPTED, true);
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  /**
   * Callback received when a permissions request has been completed.
   */
  private void fetchLocation() {
    if (mLocationHelper != null) {
      mLocationHelper.checkLocationPermissions();
    }
  }

  @Override
  public void onLocationSettingsFailed() {

  }

  @Override
  public void onLocationChanged(Location location) {
    mLastLocation = location;
    // NOT THE BEST SOLUTION, THINK OF SOMETHING ELSE
    mAdapter = new ScreenSlidePagerAdapter(getFragmentManager(), 0);
    mPager.setAdapter(mAdapter);
  }

  @Override
  public void onConfigNowSelected(int num) {
    startOnboardingFor(num);
  }

  @Override
  public void onUseDefaultSelected() {
    if (mLastLocation != null) {
      // NOT THE BEST SOLUTION, THINK OF SOMETHING ELSE
      mAdapter = new ScreenSlidePagerAdapter(getFragmentManager(),0);
      mPager.setAdapter(mAdapter);
    }
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
  }

  @Override
  public void onPageSelected(int position) {
    if (position == 1) {
      if (mAdapter.mKaabaLocatorFragment != null &&
          PermissionUtil.hasSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        mAdapter.mKaabaLocatorFragment.showMap();
      }
    } else {
      mAdapter.mKaabaLocatorFragment.hideMap();
    }
  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }

  private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private int mCardIndex;
    public KaabaLocatorFragment mKaabaLocatorFragment;

    public ScreenSlidePagerAdapter(FragmentManager fm, int index) {
      super(fm);
      mCardIndex = index;
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          if (AppSettings.getInstance(getApplicationContext()).isDefaultSet()) {
            return SalaatTimesFragment.newInstance(mCardIndex, mLastLocation);
          } else {
            return InitialConfigFragment.newInstance();
          }
        case 1:
          return mKaabaLocatorFragment = KaabaLocatorFragment.newInstance(mLastLocation);
      }
      return null;
    }

    @Override
    public int getCount() {
      return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      if (position == 0) {
        return getString(R.string.salaat_times);
      } else {
        return getString(R.string.kaaba_position);
      }
    }


  }
}
