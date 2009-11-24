package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 24.11.2009
 * Time: 12:35:12
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class PlainTextAdapter extends BaseAdapter {

    /**
     *    doesn't have any parameters
     * @return null
     */
    public List<List> getParametersList() {
        return null;
    }

    /**
     *  Structured pojo root is 'text'
     * @param o text String
     * @return
     * @throws FormatAdapterException
     */
    public SimulatorPojo adaptFrom(String o) throws FormatAdapterException {
        SimulatorPojo pojo = new StructuredSimulatorPojo();
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("text",o==null?"":o.toString());
        pojo.setRoot(root);
        return pojo;
    }

    /**
     *
     * @param pojo SimulatorPojo object
     * @return text as String
     * @throws FormatAdapterException
     */
    public String adaptTo(SimulatorPojo pojo) throws FormatAdapterException {
        return pojo.getRoot().get("text").toString();
    }

    /**
     *  empty method.
     * @throws FormatAdapterException
     */
    @Override
    void validateParameters() throws FormatAdapterException {

    }
}
