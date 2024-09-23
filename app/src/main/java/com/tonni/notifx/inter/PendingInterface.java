package com.tonni.notifx.inter;

import com.tonni.notifx.models.PendingPrice;

public interface PendingInterface {
  void UpdateUI();
  void refreshUIFromForex(PendingPrice pendingPrice);
  void addLongIdToMaster(int master_pos,long id);
}
