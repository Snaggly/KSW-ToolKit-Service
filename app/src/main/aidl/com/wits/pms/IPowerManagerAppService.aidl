package com.wits.pms;

interface IPowerManagerAppService {
        void addBooleanStatus(String str, boolean z);
        void addIntStatus(String str, int i);
        void addStringStatus(String str, String str2);
        String getJsonConfig(String str);
        int getSettingsInt(String str);
        String getSettingsString(String str);
        boolean getStatusBoolean(String str);
        int getStatusInt(String str);
        String getStatusString(String str);
        void registerCmdListener(ICmdListener iCmdListener);
        void registerObserver(String str, IContentObserver iContentObserver);
        void saveJsonConfig(String str, String str2);
        boolean sendCommand(String str);
        boolean sendStatus(String str);
        void setSettingsInt(String str, int i);
        void setSettingsString(String str, String str2);
        void unregisterCmdListener(ICmdListener iCmdListener);
        void unregisterObserver(IContentObserver iContentObserver);
}