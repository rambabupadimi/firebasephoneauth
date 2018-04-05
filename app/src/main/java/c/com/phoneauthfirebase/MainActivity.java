package c.com.phoneauthfirebase;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import c.com.phoneauthfirebase.models.GroupModel;

public class MainActivity extends AppCompatActivity {


    SearchView searchView;

    NavigationView navigationView;
    private DrawerLayout drawerLayout;
    ImageView profileImage;
    TextView profileName;


    Toolbar toolbar;


    CardView addBanners,addCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseIDS();
        initialiseToolbar();
        initialiseNaviagationImage();
        initialiseClickListeners();

    }

    private void initialiseClickListeners()
    {
        addCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this,Group.class);
                startActivity(intent);
            }
        });

        addBanners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void initialiseNaviagationImage()
    {
        FirebaseAuth   mAuth   =   FirebaseAuth.getInstance();

        Log.i("tag","auth value is"+mAuth);
        String uid = mAuth.getUid();
        Log.i("tag","auth value id"+uid);

        if(uid==null)
        {
            Intent intent = new Intent(this,Register.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                    getReference().child("Admin").child(uid);
            databaseReference.keepSynced(true);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("tag", "datasnapshot" + dataSnapshot.getValue());

                    HashMap hashMap = (HashMap) dataSnapshot.getValue();
                    Log.i("tag", "url is" + hashMap.get("imgurl").toString());

                    profileName.setText(hashMap.get("name").toString());

                    final String url = hashMap.get("imgurl").toString();
                    if (url != null) {
                        if (url != null && url.length() > 0) {
                            Picasso.with(MainActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(profileImage, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(MainActivity.this).load(url).into(profileImage);
                                        }
                                    });
                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            initNavigationDrawer();

        }

    }

    private void initialiseIDS()
    {

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        profileImage    = (ImageView) header.findViewById(R.id.profile_image);
        profileName    = (TextView) header.findViewById(R.id.profileName);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addBanners  = (CardView) findViewById(R.id.add_banners);
        addCategories = (CardView) findViewById(R.id.add_categories);

    }

    private void initialiseToolbar()
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();
                Intent intent=null;
                switch (id){
                    case R.id.home:
                        Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();

                        intent = new Intent(MainActivity.this,ChatUsersListActivity.class);
                        startActivity(intent);
                      //  drawerLayout.closeDrawers();
                        break;
                    case R.id.Orders:
                        intent = new Intent(MainActivity.this, ShowOrdersList.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        break;
                    case R.id.trash:
                        Toast.makeText(getApplicationContext(),"Trash",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(MainActivity.this, SignupOrLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;

                }
                return true;
            }
        });
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkUserExists();

    }


    private void checkUserExists()
    {
        FirebaseAuth   mAuth   =   FirebaseAuth.getInstance();

        String uid = mAuth.getUid();

        if(uid==null)
        {
            Intent intent = new Intent(this,SignupOrLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate( R.menu.search_menu, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }

                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);

               // searchRecyclerview(s);
                return true;
            }
        });
        return true;

    }


}



