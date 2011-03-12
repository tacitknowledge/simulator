package com.tacitknowledge.simulator.configuration.loaders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.ScenarioParsingException;
import com.tacitknowledge.simulator.impl.ConversationScenarioImpl;

public class ScenarioLoader
{
    private static final int FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE;

    private static final int SCENARIO_SECTIONS_COUNT = 3;

    private static final Pattern SCENARIO_REGEX = Pattern.compile(
            "\\s*\\[language\\](.+)\\[when\\](.+)\\[execute\\](.+)", FLAGS);

    /**
     * Parses scenario from given '.scn' file and returns ConversationScenario object
     * 
     * @param fileName file to be parsed
     * @return ConversationScenario
     * @throws IOException
     * @throws ScenarioParsingException 
     */
    public ConversationScenario parseScenarioFromFile(String fileName) throws IOException,
            ScenarioParsingException
    {
        InputStream is = new FileInputStream(fileName);

        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            Matcher m = SCENARIO_REGEX.matcher(sb.toString());
            if (!m.matches() || m.groupCount() != SCENARIO_SECTIONS_COUNT)
            {
                String msg = String.format("Unable to parse scenario from '%s'", fileName);
                throw new ScenarioParsingException(msg);
            }

            String language = m.group(1).trim().toLowerCase();
            String condition = m.group(2).trim();
            String execute = m.group(3).trim();

            ConversationScenario scenario = new ConversationScenarioImpl(fileName, language,
                    condition, execute);

            return scenario;
        }
        finally
        {
            is.close();
        }
    }

}
