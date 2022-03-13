package io.agora.openlive.audio.data;

import java.util.ArrayList;

import io.agora.openlive.R;
import io.agora.openlive.audio.model.AudioItem;

public class AudioData {
    public static ArrayList<AudioItem> mAudioItems = new ArrayList<AudioItem>(); ;
    public static void createGmailList() {
        mAudioItems.add(new AudioItem(R.drawable.sol7,"Back To Hometown","Sol7","https://data25.chiasenhac.com/download2/2205/0/2204427-9de2f37c/128/Back%20To%20HomeTown%20-%20Sol7.mp3"));
        mAudioItems.add(new AudioItem(R.drawable.lau_dai_tinh_ai,"Lâu đài tình ái","Đàm Vĩnh Hưng","https://data25.chiasenhac.com/download2/2209/0/2208029-1f2c2b2a/128/Lau%20Dai%20Tinh%20Ai%20-%20Dam%20Vinh%20Hung.mp3"));
        mAudioItems.add(new AudioItem(R.drawable.iceman,"Iceman","Sol7, MCK","https://data.chiasenhac.com/down2/2177/0/2176275-6788b67a/128/iceMan%20-%20Sol7_%20RPT%20MCK.mp3"));
        mAudioItems.add(new AudioItem(R.drawable.ngon_nen,"Ngọn nến","Coldzy","https://data25.chiasenhac.com/download2/2206/0/2205596-de70d0a8/128/Ngon%20Nen%20-%20Coldzy.mp3"));
        mAudioItems.add(new AudioItem(R.drawable.emd,"Xé đôi","EmD","https://data25.chiasenhac.com/download2/2209/0/2208368-f0fcbcef/128/Xe%20Doi%20-%20EMD.mp3"));
        mAudioItems.add(new AudioItem(R.drawable.peanut,"Lạc","Rhymastic","https://data25.chiasenhac.com/download2/2203/0/2202816-deb315f1/128/Lac%20-%20Rhymastic.mp3"));
        mAudioItems.add(new AudioItem(R.drawable.twocan,"Bài này vui phết","2Can","https://data25.chiasenhac.com/download2/2204/0/2203135-85b64fb3/128/Bai%20Nay%20Vui%20Phet%20-%202Can.mp3"));
        mAudioItems.add(new AudioItem(R.drawable.blacka,"Sài Gòn có em","Blacka","https://data25.chiasenhac.com/download2/2204/0/2203141-07f78d6c/128/Sai%20Gon%20Co%20Em%20-%20Blacka.mp3"));
    }
}
