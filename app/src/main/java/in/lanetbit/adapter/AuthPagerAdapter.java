package in.lanetbit.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import in.lanetbit.fragment.SignInFragment;
import in.lanetbit.fragment.SignUpFragment;

public class AuthPagerAdapter extends FragmentStatePagerAdapter {

    int tabCount;

    public AuthPagerAdapter(FragmentManager supportFragmentManager, int tabCount) {
    super(supportFragmentManager);
    this.tabCount=tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                SignInFragment signInFragment = new SignInFragment();
                return signInFragment;
            case 1:
                SignUpFragment sinUpFragment = new SignUpFragment();
                return sinUpFragment;
            default:
            return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
