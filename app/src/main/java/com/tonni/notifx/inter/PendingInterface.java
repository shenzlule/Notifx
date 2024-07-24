package com.tonni.notifx.inter;

import com.tonni.notifx.models.PendingPrice;

public interface PendingInterface {
  void UpdateUI();
  void refreshUIFromForex(PendingPrice pendingPrice);
}
