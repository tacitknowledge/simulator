package com.tacitknowledge.simulator.configuration.loaders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tacitknowledge.simulator.Scenario;
import com.tacitknowledge.simulator.ScenarioParsingException;
import com.tacitknowledge.simulator.impl.ScenarioFactory;

public class ScenarioLoader
{
    private static final int FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE;

    private static final int SCENARIO_SECTIONS_COUNT = 3;
    
    private static final int LANGUAGE_SECTION_INDEX = 1;
    
    private static final int CONDITION_SECTION_INDEX = 2;
    
    private static final int EXECUTE_SECTION_INDEX = 3;

    private static final Pattern SCENARIO_REGEX = Pattern.compile(
            "\\s*\\[language\\](.+)\\[when\\](.+)\\[execute\\](.+)", FLAGS);
    
    private static final String NEW_LINE = "\n";

    private ScenarioFactory scenarioFactory;
    
    public ScenarioLoader(ScenarioFactory scenarioFactory)
    {
        this.scenarioFactory = scenarioFactory;
    }
    
    /**
     * Parses scenario from given '.scn' file and returns ConversationScenario object
     * 
     * @param fileName file to be parsed
     * @return ConversationScenario
     * @throws IOException
     * @throws ScenarioParsingException 
     */
    public Scenario parseScenarioFromFile(String fileName) throws IOException,
            ScenarioParsingException
    {
        InputStream is = new FileInputStream(fileName);

        try
        {
            String scriptText = readWholeInputStream(is);
            Matcher matcher = matchScriptStructure(scriptText);
            String language = matcher.group(LANGUAGE_SECTION_INDEX).trim().toLowerCase();
            String condition = matcher.group(CONDITION_SECTION_INDEX).trim();
            String execute = matcher.group(EXECUTE_SECTION_INDEX).trim();
            Scenario result = scenarioFactory.createConversationScenario(
                    fileName, language, condition, execute);
            return result;
        }
        catch (IllegalArgumentException e)
        {
            throw new ScenarioParsingException("Scenario " + fileName + "could not be parsed", e);
        }
        finally
        {
            is.close();
        }
    }

    private String readWholeInputStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        StringBuilder result = new StringBuilder();

        while ((line = reader.readLine()) != null)
        {
            result.append(line + NEW_LINE);
        }
        
        return result.toString();
    }
    
    private Matcher matchScriptStructure(String scriptText) throws ScenarioParsingException {
        Matcher matcher = SCENARIO_REGEX.matcher(scriptText);
        
        if (!matcher.matches() || matcher.groupCount() != SCENARIO_SECTIONS_COUNT)
        {
            throw new IllegalArgumentException("Unable to parse the scenario" + scriptText);
        }
        
        return matcher;
    }
}
