package com.tacitknowledge.simulator.utils;

import java.io.File;
import java.io.FileFilter;

/** @author Adrian Neaga (aneaga@tacitknowledge.com) */
public class OnlyDirectoriesFilter implements FileFilter
{
    public boolean accept(File pathname)
    {
        return pathname.isDirectory() && !pathname.getName().startsWith(".");
    }
}
