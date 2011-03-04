package com.tacitknowledge.simulator.configuration.loaders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.impl.ConversationScenarioImpl;

public class ScenarioLoader
{
    private ScenarioLoader()
    {

    }

    /**
     * Parses scenario from given '.scn' file and returns ConversationScenario object
     * 
     * @param fileName file to be parsed
     * @return ConversationScenario
     * @throws IOException
     */
    public static ConversationScenario parseScenarioFromFile(String fileName) throws IOException
    {
        InputStream is = new FileInputStream(fileName);

        try
        {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;

            StringBuilder sbWhen = new StringBuilder();
            StringBuilder sbExec = new StringBuilder();

            Boolean whenFinished = false;

            while ((line = reader.readLine()) != null)
            {
                if (line.startsWith("[when]"))
                    continue;

                if (line.startsWith("[execute]"))
                {
                    whenFinished = true;
                    continue;
                }

                if (whenFinished)
                {
                    sbExec.append(line + "\n");
                }
                else
                {
                    sbWhen.append(line + "\n");
                }
            }

            //TODO : replace hard-coded "JavaScript"
            return new ConversationScenarioImpl(1, "javascript", sbWhen.toString(),
                    sbExec.toString());
        }
        finally
        {
            is.close();
        }
    }

}
