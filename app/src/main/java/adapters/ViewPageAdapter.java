package adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

// An adapter for tabs
public class ViewPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> firstFragment = new ArrayList<>();
    private final List<String> firstTitles = new ArrayList<>();

    public ViewPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return firstFragment.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return firstTitles.get(position);
    }

    @Override
    public int getCount() {
        return firstTitles.size();
    }

    public void addFragment(Fragment fragment, String title) {
        firstFragment.add(fragment);
        firstTitles.add(title);
    }
}
