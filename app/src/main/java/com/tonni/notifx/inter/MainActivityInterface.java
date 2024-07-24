package com.tonni.notifx.inter;

import androidx.fragment.app.Fragment;

import com.tonni.notifx.frags.ForexFragment;
import com.tonni.notifx.frags.PendingFragment;

public interface MainActivityInterface {
  void MakeConnThruInter();
  PendingFragment UpdatePendingMainActivity();
  ForexFragment UpdateForexMainActivity();
}
