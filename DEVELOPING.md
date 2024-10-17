# Development / Tech Notes
Here's what to know from a tech perspective.

**⚠️ Security note: Do not forget to run the `lefthook` setup below, including the setup of `talisman`. This ensures that secrets are cought before reaching the remote repository**

## Git Hooks Setup
This repository uses Git hooks for
* preventing accidentally pushing secrets or other sensitive information

In order to make use of these, do install the following tools:
* [`lefthook`](https://github.com/evilmartians/lefthook) (Git hooks)
* [`talisman`](https://thoughtworks.github.io/talisman/docs) (scans for secrets)

then install the hooks via
```bash
lefthook install
```