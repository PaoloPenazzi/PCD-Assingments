package it.unibo.pcd.assignment.event.report;

import java.util.List;

public interface InterfaceReport {
    String getFullInterfaceName();

    String getSrcFullFileName();

    List<String> getAllMethodsName();
}
