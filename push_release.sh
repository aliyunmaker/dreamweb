version=releases/$(date "+%Y%m%d_%H%M")
git tag ${version}
git push --tags