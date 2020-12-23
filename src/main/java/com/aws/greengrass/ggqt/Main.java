/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.aws.greengrass.ggqt;

import java.util.*;

public class Main {
    enum Dest {
        GTD, GROUP, ROOT, REGION, NONE
    }
    final String[] args;
    public static void main(String[] cmd) {
        System.exit(new Main(cmd).exec());
    }
    public Main(String[] args) {
        this.args = args;
    }
    int exec() {
        Dest dest = Dest.NONE;
        ArrayList<String> files = new ArrayList<>();
        TemplateCommand tc = new TemplateCommand();
        for (String s : args)
            switch (dest) {
                case GTD:
                    tc.generatedTemplateDirectory = s;
                    dest = Dest.NONE;
                    break;
                case GROUP:
                    tc.group = s;
                    dest = Dest.NONE;
                    break;
                case ROOT:
                    tc.rootPath = s;
                    dest = Dest.NONE;
                    break;
                case REGION:
                    dest = Dest.NONE;
                    tc.cloud = CloudOps.of(s);
                    break;
                default:
                    switch (s) {
                        case "-r":
                        case "--ggcRootPath":
                            dest = Dest.ROOT;
                            break;
                        case "-gtd":
                            dest = Dest.GTD;
                            break;
                        case "-g":
                        case "--group":
                            dest = Dest.GROUP;
                            break;
                        case "-dr":
                        case "--dryrun":
                            tc.dryrun = true;
                            break;
                        case "--to":
                        case "-to":
                            dest = Dest.REGION;
                            tc.dryrun = true;
                            break;
                        case "--upload":
                        case "-u":
                            tc.cloud = CloudOps.dflt();
                            tc.dryrun = true;
                            break;
                        default:
                            files.add(s);
                            break;
                    }
                    break;
            }
        tc.files = files.toArray(new String[files.size()]);
        try {
            tc.run();
            return 0;
        } catch (Throwable t) {
            Throwable c = t;
            while (c.getCause() != null)
                c = c.getCause();
            String m = c.getLocalizedMessage();
            if (m == null || m.length() == 0) m = c.toString();
            System.out.println(m);
            return 1;
        }
    }
}