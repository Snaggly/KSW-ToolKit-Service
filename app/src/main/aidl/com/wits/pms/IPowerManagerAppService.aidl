package com.wits.pms;

interface IPowerManagerAppService {
/*1*/        boolean sendCommand(String str);
/*2*/        boolean sendStatus(String str);
/*3*/        void registerCmdListener(com.wits.pms.ICmdListener iCmdListener);
/*4*/        void unregisterCmdListener(com.wits.pms.ICmdListener iCmdListener);
/*5*/        void registerObserver(String str, com.wits.pms.IContentObserver iContentObserver);
/*6*/        void unregisterObserver(com.wits.pms.IContentObserver iContentObserver);
/*7*/        boolean getStatusBoolean(String str);
/*8*/        int getStatusInt(String str);
/*9*/        String getStatusString(String str);
/*10*/       int getSettingsInt(String str);
/*11*/       String getSettingsString(String str);
/*12*/       void setSettingsInt(String str, int i);
/*13*/       void setSettingsString(String str, String str2);
/*14*/       void addIntStatus(String str, int i);
/*15*/       void addBooleanStatus(String str, boolean z);
/*16*/       void addStringStatus(String str, String str2);
/*17*/       void saveJsonConfig(String str, String str2);
/*18*/       String getJsonConfig(String str);
}