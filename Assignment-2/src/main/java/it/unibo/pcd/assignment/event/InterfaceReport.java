package it.unibo.pcd.assignment.event;

import java.util.List;

public interface InterfaceReport {
    String getFullInterfaceName();

    String getSrcFullFileName();

    List<String> getAllMethodsName();
}
