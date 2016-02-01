/*
 * Copyright (C) 2015 Olmo Gallegos Hernández.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.voghdev.prjdagger2.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.RendererAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import es.voghdev.prjdagger2.R;
import es.voghdev.prjdagger2.global.DaggerApplication;
import es.voghdev.prjdagger2.global.di.DaggerUserListComponent;
import es.voghdev.prjdagger2.global.di.UserListComponent;
import es.voghdev.prjdagger2.global.di.UserListModule;
import es.voghdev.prjdagger2.global.model.User;
import es.voghdev.prjdagger2.ui.presenter.UserListPresenter;
import es.voghdev.prjdagger2.ui.presenter.impl.UserListPresenterImpl;
import es.voghdev.prjdagger2.ui.renderer.UserRenderer;
import es.voghdev.prjdagger2.ui.renderer.UserRendererBuilder;

public class UserListActivity extends BaseActivity implements UserListPresenter.View{
    @InjectView(R.id.users_list)
    RecyclerView recyclerView;

    @InjectView(R.id.users_progressBar)
    ProgressBar progressBar;

    @Inject
    RVRendererAdapter<User> adapter;

    @Inject
    UserListPresenter presenter;

    private UserListComponent component;

    final UserRenderer.OnUserClicked mUserClickListener = new UserRenderer.OnUserClicked() {
        @Override
        public void onPictureClicked(User user) {
            presenter.onUserPictureClicked(user);
        }

        @Override
        public void onBackgroundClicked(User user) {
            presenter.onUserRowClicked(user);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component().inject(this);

        presenter.setView(this);
        presenter.initialize();

        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_users_list;
    }

    @Override
    public void showUserList(List<User> users) {
        for(User u : users)
            adapter.add(u);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void showUserListError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoInternetMessage() {
        Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void makeUserSayHello(User user) {
        Toast.makeText(this, getString(R.string.user_greeting, user.getName()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showUserClickedMessage(User user) {
        Toast.makeText(this, getString(R.string.user_was_clicked, user.getId()), Toast.LENGTH_LONG).show();
    }

    private UserListComponent component(){
        if(component == null){
            component = DaggerUserListComponent.builder()
                    .rootComponent(((DaggerApplication)getApplication()).getComponent())
                    .userListModule(new UserListModule(getApplicationContext(), mUserClickListener))
                    .build();
        }
        return component;
    }
}