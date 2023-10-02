package org.raylab;

import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import org.json.*;

public class FileDiff {

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.out.println("Usage: ContentPairer <sourceFilePath> <targetFilePath>")-;
    }

    String sourceFilePath = args[0];
    String targetFilePath = args[1];

    List<String> sourceLines = Files.readAllLines(Paths.get(sourceFilePath));
    List<String> targetLines = Files.readAllLines(Paths.get(targetFilePath));

    List<ContentPair> pairs = pairContent(sourceLines, targetLines);

    JSONArray results = new JSONArray();
    for (ContentPair pair : pairs) {
      JSONObject obj = new JSONObject();
      obj.put("source-line", pair.source);
      obj.put("target-line", pair.target);
      results.put(obj);

    }

    System.out.println(results.toString(2));
  }


  public static List<ContentPair> pairContent(List<String> source, List<String> target) {
    List<ContentPair> pairs = new ArrayList<>();

    int srcIndex = 0, tgtIndex = 0;

    while (srcIndex < source.size() || tgtIndex < target.size()) {
      int nextCommonInSource = findNextCommonIndex(source, srcIndex, target, tgtIndex);
      int nextCommonInTarget = findNextCommonIndex(target, tgtIndex, source, srcIndex);

      String sourceContent = String.join("\n", source.subList(srcIndex, nextCommonInSource));
      String targetContent = String.join("\n", target.subList(tgtIndex, nextCommonInTarget));

      if (nextCommonInSource < source.size() && nextCommonInTarget < target.size()) {
        if (source.get(nextCommonInSource).equals(target.get(nextCommonInTarget))) {
          nextCommonInSource++;
          nextCommonInTarget++;
        }
      }

      pairs.add(new ContentPair(sourceContent, targetContent));

      srcIndex = nextCommonInSource;
      tgtIndex = nextCommonInTarget;
    }

    return pairs;
  }

  private static int findNextCommonIndex(List<String> source, int srcStart, List<String> target, int tgtStart) {
    for (int i = srcStart; i < source.size(); i++) {
      for (int j = tgtStart; j < target.size(); j++) {
        if (source.get(i).equals(target.get(j))) {
          return i;
        }
      }
    }
    return source.size();
  }



  static class ContentPair {
    final String source;
    final String target;

    public ContentPair(String source, String target) {
      this.source = source;
      this.target = target;
    }
  }


}
