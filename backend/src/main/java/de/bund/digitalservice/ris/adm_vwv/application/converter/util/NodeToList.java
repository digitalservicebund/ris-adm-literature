package de.bund.digitalservice.ris.adm_vwv.application.converter.util;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@UtilityClass
public class NodeToList {

  public static List<Node> toList(NodeList nodeList) {
    List<Node> nodes = new ArrayList<>();
    for (int i = 0; i < nodeList.getLength(); i++) {
      nodes.add(nodeList.item(i));
    }
    return nodes;
  }

  public static List<Node> toList(NamedNodeMap namedNodeMap) {
    List<Node> nodes = new ArrayList<>();
    for (int i = 0; i < namedNodeMap.getLength(); i++) {
      nodes.add(namedNodeMap.item(i));
    }
    return nodes;
  }

  public static List<Pair<String, String>> toAttributePairs(NamedNodeMap namedNodeMap) {
    return toList(namedNodeMap)
      .stream()
      .map(attributeNode -> Pair.of(attributeNode.getNodeName(), attributeNode.getNodeValue()))
      .toList();
  }
}
