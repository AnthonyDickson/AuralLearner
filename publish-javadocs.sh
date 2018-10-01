#!/usr/bin/env bash
# /docs directory must be ignored by git for this to work.

# Switch to gh-pages branch
git checkout -b gh-pages || git checkout gh-pages

# Replace directory contents with docs
mkdir ../tmp
mv docs/* ../tmp
rm -rf ./*
mv ../tmp/* .
rm -rf ../tmp

# Commit and push.
git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"

git remote -v
# Might have to do the below
git remote rm origin
git remote add origin https://eight0153:$GITHUB_TOKEN@github.com/eight0153/AuralLearner

git add . -f
git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
git push -f origin gh-pages
