package it.unibo.pcd.assignment.parser.report;

import it.unibo.pcd.assignment.parser.ProjectElem;

import java.util.List;

public interface InterfaceReport extends ProjectElem {
    String getFullInterfaceName();

    String getSrcFullFileName();

    List<String> getAllMethodsName();
}
