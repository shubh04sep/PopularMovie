package com.shubham.popularmovie.fragments;
/**
 * Created by shubham on 1/2/16.
 */

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.shubham.popularmovie.R;
import com.shubham.popularmovie.activity.MovieDetailActivity;
import com.shubham.popularmovie.activity.MoviesListActivity;
import com.shubham.popularmovie.adapters.MoviesListAdapter;
import com.shubham.popularmovie.api.ApiConstants;
import com.shubham.popularmovie.api.RestClient;
import com.shubham.popularmovie.base.ApplicationController;
import com.shubham.popularmovie.base.BaseActivity;
import com.shubham.popularmovie.model.movie_api.MoviesResponseBean;
import com.shubham.popularmovie.utility.AppConstants;
import com.shubham.popularmovie.utility.EndlessScrollGridLayoutManager;
import com.shubham.popularmovie.utility.SnackBarBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MoviesListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private ProgressBar mProgressBar;
    private MoviesListAdapter mAdapter;
    private RecyclerView mMoviesListRecyclerView;
    private int mPagination = 1;
    private ArrayList<MoviesResponseBean.MoviesResult> moviesResultsList = new ArrayList<>();
    private String mSortByParam = ApiConstants.POPULARITY_DESC;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isSortApplied;
    protected Snackbar mSnackBar;
    final int mSpanCount=2;
    private BaseActivity activity;
    private View view;

    private EndlessScrollGridLayoutManager mGridLayoutManager;



    @Override
    public void onAttach(Context context) {
        if (context instanceof MoviesListActivity)
            activity=(MoviesListActivity)context;

        super.onAttach(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_list, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view=view;
        intUI(view);

    }


    @Override
    public void onPause() {
        super.onPause();
        if (mSnackBar != null)
            mSnackBar.dismiss();
    }

    private void intUI(View view)
    {
        mMoviesListRecyclerView = (RecyclerView) view.findViewById(R.id.movies_list_recycler_view);
        // use a Grid layout manager

        mSwipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mProgressBar=(ProgressBar)view.findViewById(R.id.progressBar);
       mGridLayoutManager = new EndlessScrollGridLayoutManager(getActivity(),mSpanCount) {
        @Override
        public void onBottomReached() {
            if (ApplicationController.getApplicationInstance().isNetworkConnected()) {
                mPagination = mPagination + 1;
                getMoviesList(View.VISIBLE);
            } else
                mSnackBar = SnackBarBuilder.make(getActivity().getWindow().getDecorView(), getString(R.string.no_internet_connection)).build();

        }

        @Override
        public void onTopReached() {

        }

        @Override
        public void onScrollDown() {

        }

        @Override
        public void onScrollUp() {

        }
    } ;
        mMoviesListRecyclerView.setLayoutManager(mGridLayoutManager);

        setListAdapter(view);
        getMoviesList(View.VISIBLE);

    }

    private void setListAdapter(final View view) {
        if (mAdapter == null) {
            mAdapter = new MoviesListAdapter(getActivity(),moviesResultsList) {
                @Override
                public void onItemClick(int position) {
                    super.onItemClick(position);
                    Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                    intent.putExtra(AppConstants.EXTRA_INTENT_PARCEL, moviesResultsList.get(position));
                    Pair<View, String> p6 = Pair.create(view, "row");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p6);
                    startActivity(intent,options.toBundle());

                }
            };
            mMoviesListRecyclerView.setAdapter(mAdapter);
            mMoviesListRecyclerView.scheduleLayoutAnimation();

        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getMoviesList(int progressBarVisibility) {
        if (ApplicationController.getApplicationInstance().isNetworkConnected()) {
            mProgressBar.setVisibility(progressBarVisibility);
            HashMap<String, String> stringHashMap = new HashMap<>();
            stringHashMap.put(ApiConstants.PARAM_SORT_BY, mSortByParam);
            stringHashMap.put(ApiConstants.PARAM_API_KEY, ApiConstants.API_KEY);
            stringHashMap.put(ApiConstants.PARAM_PAGE, mPagination + "");

            Call<MoviesResponseBean> beanCall = RestClient.getInstance().getApiServices().apiMoviesList(stringHashMap);
            beanCall.enqueue(new Callback<MoviesResponseBean>() {
                @Override
                public void onResponse(Response<MoviesResponseBean> response, Retrofit retrofit) {
                    mProgressBar.setVisibility(View.GONE);
                    MoviesResponseBean responseBean = response.body();
                    moviesResultsList.addAll(responseBean.getResults());
                    setListAdapter(view);


                    if (responseBean.getResults().isEmpty())
                        Log.i("Retro", response.toString());
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.i("Retro", t.toString());
                }
            });
        } else {
            mSnackBar = SnackBarBuilder.make(getActivity().getWindow().getDecorView(), getString(R.string.no_internet_connection))
                    .setActionText(getString(R.string.retry))
                    .onSnackBarClicked(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getMoviesList(View.VISIBLE);
                        }
                    })
                    .build();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most_popular:
                isSortApplied = true;
                mSortByParam = ApiConstants.POPULARITY_DESC;
                onRefresh();
                return true;
            case R.id.highest_rated:
                mSortByParam = ApiConstants.HIGHEST_RATED;
                isSortApplied = true;
                onRefresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (ApplicationController.getApplicationInstance().isNetworkConnected()) {
            mPagination = 1;
            moviesResultsList.clear();
            try {
                mAdapter.notifyDataSetChanged();
                mAdapter = null;
            } catch (Exception ignored) {
            }

            if (isSortApplied) {
                isSortApplied = false;
            } else {
                mSortByParam = ApiConstants.POPULARITY_DESC;
            }
            getMoviesList(View.VISIBLE);
        } else {
            mSnackBar = SnackBarBuilder.make(getActivity().getWindow().getDecorView(), getString(R.string.no_internet_connection)).build();
        }
    }
}
