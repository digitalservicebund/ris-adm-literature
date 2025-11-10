package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.adapter;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.NamedNodeMap;
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

  /**
   * Returns a list of nodes from the given node map.
   * @param namedNodeMap The node map
   * @return List of nodes
   */
  public static List<Node> toList(NamedNodeMap namedNodeMap) {
    List<Node> nodes = new ArrayList<>();
    for (int i = 0; i < namedNodeMap.getLength(); i++) {
      nodes.add(namedNodeMap.item(i));
    }
    return nodes;
  }

  /**
   * Returns a list of pairs where the left side is the key and the right side the value of an attribute from
   * the given node map.
   * @param namedNodeMap The node map
   * @return List of attribute pairs
   */
  public static List<Pair<String, String>> toAttributePairs(NamedNodeMap namedNodeMap) {
    return toList(namedNodeMap)
      .stream()
      .map(attributeNode -> Pair.of(attributeNode.getNodeName(), attributeNode.getNodeValue()))
      .toList();
  }
}
