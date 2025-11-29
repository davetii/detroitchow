# Claude Code Quick Start Guide

**For Dave: How to start each new Claude Code session in the DetroitChow project**

## 🚀 Recommended Session Starter

When starting a new Claude Code chat in this project, say:

```
Review CLAUDE.md and relevant documentation before starting.
I'm working on [frontend/backend/database] code.
```

Or even simpler:

```
Read docs first
```

## Why?

While Claude Code **automatically reads CLAUDE.md**, it may not automatically read the linked documentation files (`FRONTEND_PATTERNS.md`, `TESTING.md`, etc.) unless you prompt it to.

## What This Does

This prompt will cause Claude Code to:
1. ✅ Read `CLAUDE.md` (automatic anyway)
2. ✅ Read `detroitchow-admin-ui/FRONTEND_PATTERNS.md` (for React/TypeScript patterns)
3. ✅ Read `detroitchow-admin-ui/TESTING.md` (for testing patterns)
4. ✅ Read `detroitchow-admin-ui/README.md` (for project setup)

## Alternative: Area-Specific Starters

**For frontend work:**
```
Working on React frontend. Review FRONTEND_PATTERNS.md and TESTING.md first.
```

**For backend work:**
```
Working on Spring Boot backend. Review CLAUDE.md first.
```

**For database work:**
```
Working on database migrations. Review database/liquibase/README.md first.
```

## Documentation Structure

```
detroitchow/
├── CLAUDE.md                              # Auto-read by Claude Code
│                                          # Now includes directive to read other docs
├── CLAUDE_CODE_QUICKSTART.md              # This file (for your reference)
│
└── detroitchow-admin-ui/
    ├── FRONTEND_PATTERNS.md               # TypeScript/React patterns
    ├── TESTING.md                         # Testing guide with examples
    └── README.md                          # Project setup
```

## Key Patterns Claude Code Should Know

From the documentation, Claude Code should understand:

### TypeScript
- ❌ **NEVER** create `.js` or `.jsx` files
- ✅ **ALWAYS** use `.tsx` (for JSX) or `.ts` (no JSX)

### Testing
- ❌ **NEVER** use `.test.ts` for files with JSX
- ✅ **ALWAYS** use `.test.tsx` when test has JSX
- ✅ **ALWAYS** `import React from 'react'` in `.tsx` files

### API Integration
- Backend returns camelCase: `placeId`, `formattedAddress`
- Spec defines snake_case: `place_id`, `formatted_address`
- Map in hooks (see `useGooglePlace.ts` example)

### Custom Hooks
- Start with `use`
- Return `null` (not `undefined`)
- Use `enabled` for conditional queries

## If Claude Code Makes Mistakes

**If it creates `.jsx` files:**
```
STOP. Review FRONTEND_PATTERNS.md - we use .tsx, not .jsx
```

**If tests fail with JSX errors:**
```
STOP. Review TESTING.md - test files with JSX need .tsx extension
```

**If it returns undefined from hooks:**
```
STOP. Review FRONTEND_PATTERNS.md - return null, not undefined
```

## Testing the Session Start

To verify Claude Code read the docs, you can ask:
```
What file extension should I use for React components in this project?
```

Expected answer: `.tsx` (never `.jsx`)

## Notes

- Claude Code reads `CLAUDE.md` automatically at session start
- But it may not read linked docs unless prompted
- The documentation now has **⚠️ MANDATORY READING** headers to emphasize importance
- CLAUDE.md now explicitly tells Claude Code to read the other docs
- You shouldn't need to repeat this every message, just at session start

---

**Keep this file as your reference for starting new Claude Code sessions!**
