# Modeling Controversy Risk on Wikipedia

This project seeks to score the level of controversy of articles and topics in Wikipedia using algorithm(s).

Possible applications include improving human understanding on how to consume information, and inclusion into other algorithms for predicting dispute or disinformation.

# Background

Wikipedia curates special pages with lists of controversial topics.  Lists of controversies are useful subject matter expertise when understanding disputes (and for the maintainers of wikipedia, dealing with the disputes).  On the other hand, human curated pages are inherently limited by the attention of the humans maintaining the list.

An algorithmic score for controversy has advantages over human curated labels.  For example, I want to be able to explain to my kids what types of subject matter are most controversial and thus require skeptical look.  I also want to be able to explain to my kids what types of subject matter have the most agreement (least controversy).  Both aspirations require short representative lists that have been prioritized with something like a quantitative score.

An automatic controversy score might also prove useful in building bigger models for dispute and/or disinformation.  The score could be used as a generated feature for a supervised learning model, or for picking representative examples of controversy across a wide variety of subjects.

Crafting the controversy score to mimicking an existing a priori notion of controversy is out of current scope.

# Roadmap

| Task | Status |
|---|---|
| Parse compressed wikipedia edit data for all historical versions | Complete |
| Parse compressed wikipedia edit data for all users | TODO |
| Detect a computationally efficient test for literal full revert | Complete |
| Detect a more computationally involved test for controversy | TODO |
| For fast development, extract the full edit history of a small random sample of article IDs | TODO |
| Walk the full corpus of article edits, collecting 1) aggregate statistics for all articles, and 2) full article history on some articles | TODO |
| Build a second controversy score that is allowed to be less computationally efficient | TODO |
| Build a model for high risk articles using an interpretable model e.g. naive bayes or regression tree | TODO |
| Rewalk the full corpus of edits.  To mitigate computational cost, apply the expensive measure of controversy only when the inexpensive score exceeds a threshold. | TODO |

# Implementation

## Implementation: Test the code WIP

An automated test suite can be run with JDK 8 and sbt, with the command:

    sbt test

## Implementation: Testing data

Automated tests so far run against three sources: large, medium, and small.

The large sized source was retrieved Aug 7, 2019 with:

    wget https://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-meta-history1.xml-p1043p2036.7z

In order to conserve code repository size, the "medium sized source" was filtered from the large source with:

    7z x -so enwiki-latest-pages-meta-history1.xml-p1043p2036.7z enwiki-latest-pages-meta-history1.xml-p1043p2036 | head -c300000000 | 7z a -si enwiki-latest-pages-meta-history1.xml-p1043p2036-300MB.7z

And committed to the repository under the directory "data".

Finally, the small source was hand-crafted from salient examples from the medium source.

# Related Work

Wikipedia curates special pages with lists of controversial topics, including:

    https://en.wikipedia.org/wiki/Category:Wikipedia_controversial_topics

Wikipedia considers controversy to be one of many kinds of dispute on wikipedia:

    https://en.wikipedia.org/wiki/Category:Wikipedia_disputes

A common occurrence in controversial articles is something called "Edit warring":

    https://en.wikipedia.org/wiki/Wikipedia:Edit_warring
