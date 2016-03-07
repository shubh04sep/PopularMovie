package com.shubham.popularmovie.activity;
/**
 * Created by shubham on 1/2/16.
 */

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.shubham.popularmovie.R;
import com.shubham.popularmovie.base.BaseActivity;
import com.shubham.popularmovie.fragments.MovieDetailFragment;
import com.shubham.popularmovie.model.movie_api.MoviesResponseBean;
import com.shubham.popularmovie.utility.AppConstants;


public class MovieDetailActivity extends BaseActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        setTitle(getString(R.string.title_activity_movie_detail));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUI();

    }

    private void initUI() {
        MovieDetailFragment mMovieDetailFragment = new MovieDetailFragment();
        MoviesResponseBean.MoviesResult moviesResult=getIntent().getParcelableExtra(AppConstants.EXTRA_INTENT_PARCEL);
        Bundle bundle= new Bundle();
        bundle.putParcelable(AppConstants.EXTRA_INTENT_PARCEL,moviesResult);
        mMovieDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, mMovieDetailFragment, "detailfragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
