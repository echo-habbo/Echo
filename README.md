# Cloning Echo with Submodules

This guide explains how to properly clone the Echo repository along with all its submodules.

## What are Git Submodules?

Git submodules allow you to include other Git repositories as subdirectories within your main repository. The Echo project uses submodules to manage external dependencies and related projects.

## Quick Start

### Option 1: Clone with Submodules (Recommended)

Clone the repository and initialize all submodules in one command:

```bash
git clone --recursive https://github.com/echo-habbo/Echo.git
```

### Option 2: Clone First, Then Initialize Submodules

If you've already cloned the repository without submodules:

```bash
git clone https://github.com/echo-habbo/Echo.git
cd Echo
git submodule update --init --recursive
```

## Detailed Commands

### Initialize Submodules After Cloning

If you cloned without the `--recursive` flag:

```bash
# Initialize and clone all submodules
git submodule update --init --recursive
```

### Update Submodules

To pull the latest changes for all submodules:

```bash
# Update all submodules to their latest commits
git submodule update --remote --recursive
```