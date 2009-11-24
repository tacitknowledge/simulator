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


    public List<List> getParametersList() {
        return null;
    }

    public SimulatorPojo adaptFrom(Object o) throws FormatAdapterException {
        SimulatorPojo pojo = new StructuredSimulatorPojo();
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("text",o==null?"":o.toString());
        pojo.setRoot(root);
        return pojo;
    }

    public Object adaptTo(SimulatorPojo pojo) throws FormatAdapterException {
        return pojo.getRoot().get("text");
    }

    @Override
    void validateParameters() throws FormatAdapterException {

    }
}
