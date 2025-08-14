package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.adapter;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class for handling instances of {@link NodeList}.
 */
@UtilityClass
public class NodeToList {

  /**
   * Returns a list of nodes from the given node list.
   * @param nodeList The node list
   * @return List of nodes
   */
  public static List<Node> toList(NodeList nodeList) {
    List<Node> nodes = new ArrayList<>();
    for (int i = 0; i < nodeList.getLength(); i++) {
      nodes.add(nodeList.item(i));
    }
    return nodes;
  }
}
