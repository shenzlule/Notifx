package com.tonni.notifx.inter;

public interface ForexCurrencyInterface {
  void UpdateUI();
  void refreshUIFromPending_remove(int pos, int realPos);
  void refreshUIFromPending_add(int pos, int realPos,int pos_for_master_chain);

  void backUpData();
}
