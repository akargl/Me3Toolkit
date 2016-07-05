/*
 * The MIT License
 *
 * Copyright 2016 Chypsylon <therealchypsylon@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.chypsylon.me3toolkit.toolkit;

import eu.chypsylon.me3toolkit.util.Coalesced;
import eu.chypsylon.me3toolkit.util.Constants;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Chypsylon <therealchypsylon@gmail.com>
 */
public class FovFix {
    private static final Logger LOG = Logger.getLogger(FovFix.class.getName());
    
    public static boolean applyFovFix(Me3Toolkit me3Toolkit, int fovValue) {
        me3Toolkit.getMainUi().log("Unpacking Coalesced.bin ...");
        if (!Coalesced.binToJson(me3Toolkit.getMe3InstallPath())) {
            return false;
        }
        
        JSONParser jsonParser = new JSONParser();
        
        try {
            JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(Constants.JSON_DIRECTORY.resolve("06_bioinput.json").toString()));
            JSONArray bindings = (JSONArray)((JSONObject)((JSONObject)jsonObject.get("sections")).get("sfxgame.sfxgamemodebase")).get("bindings");
            String fovBinding = "( Name=\"Shared_Aim\", Command=\"SwapWeaponIfEmpty | TightAim | FOV 0 | OnRelease FOV " + fovValue + " | OnRelease StopTightAim\" )";
            bindings.add(fovBinding);
            
            FileWriter fileWriter = new FileWriter(Constants.JSON_DIRECTORY.resolve("06_bioinput.json").toString());
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
            fileWriter.close();
            
        } catch (IOException | ParseException e) {
            LOG.log(Level.SEVERE, null, e);
            return false;
        }
        
        me3Toolkit.getMainUi().log("Packing Coalesced.bin ...");
        boolean jsonToBinSuccess = Coalesced.jsonToBin(me3Toolkit.getMe3InstallPath());
        
        //TODO: delete json directory
        
        return jsonToBinSuccess;
    }
}