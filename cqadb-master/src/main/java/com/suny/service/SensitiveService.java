package com.suny.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 23-2-3.10:13 p.m.
 */
@Service
public class SensitiveService implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(SensitiveService.class);


    /**
     * Default Sensitive Words
     */
    private static final String DEFAULT_REPLACEMENT = "***";


    private class TrieNode {
        //          true keyword ends; false continues
        private boolean end = false;
        //        key is the next character, value is the corresponding node
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        //        Add a node tree to the specified position
        void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        /**
         * get the next node
         */
        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeywordEnd() {
            return end;
        }

        void setKeywordEnd(boolean end) {
            this.end = end;
        }

        public int getSubNodeCount() {
            return subNodes.size();
        }
    }

    /**
     * root node
     */
    private TrieNode rootNode = new TrieNode();


    /**
     * Determine if it is a symbol
     */

    private boolean isSymbol(char c) {
        int ic = (int) c;
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    /**
     * filter sensitive words
     */

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();
        TrieNode tempNode = rootNode;
        int begin = 0;  // rollback number
        int position = 0;   

        while (position < text.length()) {
            char c = text.charAt(position);
            // If it is a space, just skip it
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            //
            if (tempNode == null) {
                // 
                result.append(text.charAt(begin));
                // 
                position = begin + 1;
                begin = position;
                // 
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {
                ++position;
            }
        }
        result.append(text.substring(begin));

        return result.toString();
    }

    private void addWord(String lineTxt) {
        TrieNode tempNode = rootNode;
        // 
        for (int i = 0; i < lineTxt.length(); i++) {
            Character c = lineTxt.charAt(i);
            // filter spaces
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);
            // not initialized
            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineTxt.length() - 1) {
                // 
                tempNode.setKeywordEnd(true);
            }

        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWord.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            reader.close();
        } catch (IOException e) {
            logger.error("Failed to read sensitive word file" + e.getMessage());
        }
    }


   /* public static void main(String[] args) {
        SensitiveService sensitiveService = new SensitiveService();
        sensitiveService.addWord("shit");
        sensitiveService.addWord("damn");
        System.out.println(sensitiveService.filter("words filtered"));
    }*/

}






















