package me.opia.note.noteopia;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ntb.NavigationTabBar;
import me.opia.note.noteopia.FragmentsView.CommunityFragment;
import me.opia.note.noteopia.FragmentsView.NotesFragment;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.navigation) NavigationTabBar navigationTabBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();



        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_people_black_24dp),
                        Color.parseColor("#76afcf")
                ).title("Community")
                        .badgeTitle("state")
                        .build()
        );

        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_description_black_24dp),
                        Color.parseColor("#f9bb72")
                ).title("Notes")
                        .badgeTitle("with")
                        .build()
        );



        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 3);
        navigationTabBar.setBehaviorEnabled(true);
        navigationTabBar.setIsBadgeUseTypeface(true);


        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 200);

    }

//-------------------------------------------------------------------------------



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 1:
                    NotesFragment notesFragment = new NotesFragment();
                    return notesFragment;

                case 0:
                    CommunityFragment communityFragment = new CommunityFragment();
                    return communityFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}
