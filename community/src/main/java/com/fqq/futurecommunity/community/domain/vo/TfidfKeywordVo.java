package com.fqq.futurecommunity.community.domain.vo;

import java.math.BigDecimal;

/** TF-IDF 关键词条目。 */
public class TfidfKeywordVo {
    private String word;

    private Integer tf;

    private Integer df;

    private BigDecimal idf;

    private BigDecimal tfidf;

    private BigDecimal weight;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getTf() {
        return tf;
    }

    public void setTf(Integer tf) {
        this.tf = tf;
    }

    public Integer getDf() {
        return df;
    }

    public void setDf(Integer df) {
        this.df = df;
    }

    public BigDecimal getIdf() {
        return idf;
    }

    public void setIdf(BigDecimal idf) {
        this.idf = idf;
    }

    public BigDecimal getTfidf() {
        return tfidf;
    }

    public void setTfidf(BigDecimal tfidf) {
        this.tfidf = tfidf;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
