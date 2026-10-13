import os

project_dir = 'SantaList.xcodeproj'
os.makedirs(os.path.join(project_dir, 'xcshareddata/xcschemes'), exist_ok=True)

# Build file list
app_sources = [
    ('SantaListApp.swift', 'SantaList/App/SantaListApp.swift'),
    ('SeedData.swift', 'SantaList/App/SeedData.swift'),
    ('GiftStatus.swift', 'SantaList/Models/GiftStatus.swift'),
    ('Occasion.swift', 'SantaList/Models/Occasion.swift'),
    ('Person.swift', 'SantaList/Models/Person.swift'),
    ('Gift.swift', 'SantaList/Models/Gift.swift'),
    ('BudgetCalculator.swift', 'SantaList/Logic/BudgetCalculator.swift'),
    ('Formatters.swift', 'SantaList/Logic/Formatters.swift'),
    ('Countdown.swift', 'SantaList/Logic/Countdown.swift'),
    ('ReminderService.swift', 'SantaList/Services/ReminderService.swift'),
    ('ContentView.swift', 'SantaList/Views/ContentView.swift'),
    ('StatusChipView.swift', 'SantaList/Views/Components/StatusChipView.swift'),
    ('BudgetProgressBar.swift', 'SantaList/Views/Components/BudgetProgressBar.swift'),
    ('AppHeaderView.swift', 'SantaList/Views/Components/AppHeaderView.swift'),
    ('AddEditGiftSheet.swift', 'SantaList/Views/Components/AddEditGiftSheet.swift'),
    ('AddEditPersonSheet.swift', 'SantaList/Views/People/AddEditPersonSheet.swift'),
    ('PersonDetailView.swift', 'SantaList/Views/People/PersonDetailView.swift'),
    ('PeopleView.swift', 'SantaList/Views/People/PeopleView.swift'),
    ('TimelineView.swift', 'SantaList/Views/Timeline/TimelineView.swift'),
    ('BudgetView.swift', 'SantaList/Views/Budget/BudgetView.swift'),
]

app_resources = [
    ('Assets.xcassets', 'SantaList/Resources/Assets.xcassets', 'folder.assetcatalog'),
    ('Localizable.xcstrings', 'SantaList/Resources/Localizable.xcstrings', 'text.json'),
    ('PrivacyInfo.xcprivacy', 'SantaList/Resources/PrivacyInfo.xcprivacy', 'text.xml'),
]

test_sources = [
    ('BudgetCalculatorTests.swift', 'SantaListTests/BudgetCalculatorTests.swift'),
    ('GiftStatusTests.swift', 'SantaListTests/GiftStatusTests.swift'),
    ('OverdueLogicTests.swift', 'SantaListTests/OverdueLogicTests.swift'),
    ('CountdownTests.swift', 'SantaListTests/CountdownTests.swift'),
    ('FormattersTests.swift', 'SantaListTests/FormattersTests.swift'),
    ('SwiftDataCascadeTests.swift', 'SantaListTests/SwiftDataCascadeTests.swift'),
]

def make_id(prefix, index):
    return f'{prefix:04X}{index:020X}'

app_src_build_ids = []
for idx, (name, path) in enumerate(app_sources):
    bf_id = make_id(0x1001, idx)
    fr_id = make_id(0x2001, idx)
    app_src_build_ids.append((bf_id, fr_id, name, path))

app_res_build_ids = []
for idx, (name, path, ftype) in enumerate(app_resources):
    bf_id = make_id(0x1101, idx)
    fr_id = make_id(0x2101, idx)
    app_res_build_ids.append((bf_id, fr_id, name, path, ftype))

test_src_build_ids = []
for idx, (name, path) in enumerate(test_sources):
    bf_id = make_id(0x1201, idx)
    fr_id = make_id(0x2201, idx)
    test_src_build_ids.append((bf_id, fr_id, name, path))

infoplist_fr_id = make_id(0x2301, 1)

app_product_id = make_id(0x2401, 1)
test_product_id = make_id(0x2401, 2)

target_app_id = 'A00000000000000000000001'
target_test_id = 'A00000000000000000000002'
project_id = 'P00000000000000000000001'

app_sources_phase_id = 'B00000000000000000000001'
app_frameworks_phase_id = 'B00000000000000000000002'
app_resources_phase_id = 'B00000000000000000000003'

test_sources_phase_id = 'B00000000000000000000004'
test_frameworks_phase_id = 'B00000000000000000000005'
test_resources_phase_id = 'B00000000000000000000006'

target_dep_id = 'D00000000000000000000001'
container_proxy_id = 'C00000000000000000000001'

main_group_id = 'G00000000000000000000001'
app_group_id = 'G00000000000000000000002'
test_group_id = 'G00000000000000000000003'
products_group_id = 'G00000000000000000000004'

proj_debug_cfg_id = 'E00000000000000000000001'
proj_release_cfg_id = 'E00000000000000000000002'
proj_cfg_list_id = 'E00000000000000000000003'

app_debug_cfg_id = 'E00000000000000000000004'
app_release_cfg_id = 'E00000000000000000000005'
app_cfg_list_id = 'E00000000000000000000006'

test_debug_cfg_id = 'E00000000000000000000007'
test_release_cfg_id = 'E00000000000000000000008'
test_cfg_list_id = 'E00000000000000000000009'

lines = []
lines.append('// !$*UTF8*$!')
lines.append('{')
lines.append('\tarchiveVersion = 1;')
lines.append('\tclasses = {')
lines.append('\t};')
lines.append('\tobjectVersion = 56;')
lines.append('\tobjects = {')

# PBXBuildFile
lines.append('\n/* Begin PBXBuildFile section */')
for bf_id, fr_id, name, path in app_src_build_ids:
    lines.append(f'\t\t{bf_id} /* {name} in Sources */ = {{isa = PBXBuildFile; fileRef = {fr_id} /* {name} */; }};')
for bf_id, fr_id, name, path, ftype in app_res_build_ids:
    lines.append(f'\t\t{bf_id} /* {name} in Resources */ = {{isa = PBXBuildFile; fileRef = {fr_id} /* {name} */; }};')
for bf_id, fr_id, name, path in test_src_build_ids:
    lines.append(f'\t\t{bf_id} /* {name} in Sources */ = {{isa = PBXBuildFile; fileRef = {fr_id} /* {name} */; }};')
lines.append('/* End PBXBuildFile section */')

# PBXContainerItemProxy
lines.append('\n/* Begin PBXContainerItemProxy section */')
lines.append(f'''\t\t{container_proxy_id} /* PBXContainerItemProxy */ = {{
\t\t\tisa = PBXContainerItemProxy;
\t\t\tcontainerPortal = {project_id} /* Project object */;
\t\t\tproxyType = 1;
\t\t\tremoteGlobalIDString = {target_app_id};
\t\t\tremoteInfo = SantaList;
\t\t}};''')
lines.append('/* End PBXContainerItemProxy section */')

# PBXFileReference
lines.append('\n/* Begin PBXFileReference section */')
lines.append(f'\t\t{app_product_id} /* SantaList.app */ = {{isa = PBXFileReference; explicitFileType = wrapper.application; includeInIndex = 0; path = SantaList.app; sourceTree = BUILT_PRODUCTS_DIR; }};')
lines.append(f'\t\t{test_product_id} /* SantaListTests.xctest */ = {{isa = PBXFileReference; explicitFileType = wrapper.cfbundle; includeInIndex = 0; path = SantaListTests.xctest; sourceTree = BUILT_PRODUCTS_DIR; }};')
lines.append(f'\t\t{infoplist_fr_id} /* Info.plist */ = {{isa = PBXFileReference; lastKnownFileType = text.plist.xml; path = SantaList/Info.plist; sourceTree = "<group>"; }};')

for bf_id, fr_id, name, path in app_src_build_ids:
    lines.append(f'\t\t{fr_id} /* {name} */ = {{isa = PBXFileReference; lastKnownFileType = sourcecode.swift; path = "{path}"; sourceTree = "<group>"; }};')
for bf_id, fr_id, name, path, ftype in app_res_build_ids:
    lines.append(f'\t\t{fr_id} /* {name} */ = {{isa = PBXFileReference; lastKnownFileType = "{ftype}"; path = "{path}"; sourceTree = "<group>"; }};')
for bf_id, fr_id, name, path in test_src_build_ids:
    lines.append(f'\t\t{fr_id} /* {name} */ = {{isa = PBXFileReference; lastKnownFileType = sourcecode.swift; path = "{path}"; sourceTree = "<group>"; }};')
lines.append('/* End PBXFileReference section */')

# PBXFrameworksBuildPhase
lines.append('\n/* Begin PBXFrameworksBuildPhase section */')
lines.append(f'''\t\t{app_frameworks_phase_id} /* Frameworks */ = {{
\t\t\tisa = PBXFrameworksBuildPhase;
\t\t\tbuildActionMask = 2147483647;
\t\t\tfiles = (
\t\t\t);
\t\t\trunOnlyForDeploymentPostprocessing = 0;
\t\t}};''')
lines.append(f'''\t\t{test_frameworks_phase_id} /* Frameworks */ = {{
\t\t\tisa = PBXFrameworksBuildPhase;
\t\t\tbuildActionMask = 2147483647;
\t\t\tfiles = (
\t\t\t);
\t\t\trunOnlyForDeploymentPostprocessing = 0;
\t\t}};''')
lines.append('/* End PBXFrameworksBuildPhase section */')

# PBXGroup
lines.append('\n/* Begin PBXGroup section */')
lines.append(f'''\t\t{main_group_id} = {{
\t\t\tisa = PBXGroup;
\t\t\tchildren = (
\t\t\t\t{app_group_id} /* SantaList */,
\t\t\t\t{test_group_id} /* SantaListTests */,
\t\t\t\t{products_group_id} /* Products */,
\t\t\t);
\t\t\tsourceTree = "<group>";
\t\t}};''')

app_children = [fr_id for bf_id, fr_id, name, path in app_src_build_ids]
app_children += [fr_id for bf_id, fr_id, name, path, ftype in app_res_build_ids]
app_children.append(infoplist_fr_id)
app_children_str = '\n'.join([f'\t\t\t\t{cid},' for cid in app_children])

lines.append(f'''\t\t{app_group_id} /* SantaList */ = {{
\t\t\tisa = PBXGroup;
\t\t\tchildren = (
{app_children_str}
\t\t\t);
\t\t\tname = SantaList;
\t\t\tsourceTree = "<group>";
\t\t}};''')

test_children = [fr_id for bf_id, fr_id, name, path in test_src_build_ids]
test_children_str = '\n'.join([f'\t\t\t\t{cid},' for cid in test_children])

lines.append(f'''\t\t{test_group_id} /* SantaListTests */ = {{
\t\t\tisa = PBXGroup;
\t\t\tchildren = (
{test_children_str}
\t\t\t);
\t\t\tname = SantaListTests;
\t\t\tsourceTree = "<group>";
\t\t}};''')

lines.append(f'''\t\t{products_group_id} /* Products */ = {{
\t\t\tisa = PBXGroup;
\t\t\tchildren = (
\t\t\t\t{app_product_id} /* SantaList.app */,
\t\t\t\t{test_product_id} /* SantaListTests.xctest */,
\t\t\t);
\t\t\tname = Products;
\t\t\tsourceTree = "<group>";
\t\t}};''')
lines.append('/* End PBXGroup section */')

# PBXNativeTarget
lines.append('\n/* Begin PBXNativeTarget section */')
lines.append(f'''\t\t{target_app_id} /* SantaList */ = {{
\t\t\tisa = PBXNativeTarget;
\t\t\tbuildConfigurationList = {app_cfg_list_id} /* Build configuration list for PBXNativeTarget "SantaList" */;
\t\t\tbuildPhases = (
\t\t\t\t{app_sources_phase_id} /* Sources */,
\t\t\t\t{app_frameworks_phase_id} /* Frameworks */,
\t\t\t\t{app_resources_phase_id} /* Resources */,
\t\t\t);
\t\t\tbuildRules = (
\t\t\t);
\t\t\tdependencies = (
\t\t\t);
\t\t\tname = SantaList;
\t\t\tproductName = SantaList;
\t\t\tproductReference = {app_product_id} /* SantaList.app */;
\t\t\tproductType = "com.apple.product-type.application";
\t\t}};''')

lines.append(f'''\t\t{target_test_id} /* SantaListTests */ = {{
\t\t\tisa = PBXNativeTarget;
\t\t\tbuildConfigurationList = {test_cfg_list_id} /* Build configuration list for PBXNativeTarget "SantaListTests" */;
\t\t\tbuildPhases = (
\t\t\t\t{test_sources_phase_id} /* Sources */,
\t\t\t\t{test_frameworks_phase_id} /* Frameworks */,
\t\t\t\t{test_resources_phase_id} /* Resources */,
\t\t\t);
\t\t\tbuildRules = (
\t\t\t);
\t\t\tdependencies = (
\t\t\t\t{target_dep_id} /* PBXTargetDependency */,
\t\t\t);
\t\t\tname = SantaListTests;
\t\t\tproductName = SantaListTests;
\t\t\tproductReference = {test_product_id} /* SantaListTests.xctest */;
\t\t\tproductType = "com.apple.product-type.bundle.unit-test";
\t\t}};''')
lines.append('/* End PBXNativeTarget section */')

# PBXProject
lines.append('\n/* Begin PBXProject section */')
lines.append(f'''\t\t{project_id} /* Project object */ = {{
\t\t\tisa = PBXProject;
\t\t\tattributes = {{
\t\t\t\tBuildIndependentTargetsInParallel = 1;
\t\t\t\tLastUpgradeCheck = 1540;
\t\t\t\tTargetAttributes = {{
\t\t\t\t\t{target_app_id} = {{
\t\t\t\t\t\tCreatedOnToolsVersion = 15.0;
\t\t\t\t\t}};
\t\t\t\t\t{target_test_id} = {{
\t\t\t\t\t\tCreatedOnToolsVersion = 15.0;
\t\t\t\t\t\tTestTargetID = {target_app_id};
\t\t\t\t\t}};
\t\t\t\t}};
\t\t\t}};
\t\t\tbuildConfigurationList = {proj_cfg_list_id} /* Build configuration list for PBXProject "SantaList" */;
\t\t\tcompatibilityVersion = "Xcode 14.0";
\t\t\tdevelopmentRegion = vi;
\t\t\thasScannedForEncodings = 0;
\t\t\tknownRegions = (
\t\t\t\tvi,
\t\t\t\ten,
\t\t\t\tBase,
\t\t\t);
\t\t\tmainGroup = {main_group_id};
\t\t\tproductRefGroup = {products_group_id} /* Products */;
\t\t\tprojectDirPath = "";
\t\t\tprojectRoot = "";
\t\t\ttargets = (
\t\t\t\t{target_app_id} /* SantaList */,
\t\t\t\t{target_test_id} /* SantaListTests */,
\t\t\t);
\t\t}};''')
lines.append('/* End PBXProject section */')

# PBXResourcesBuildPhase
lines.append('\n/* Begin PBXResourcesBuildPhase section */')
app_res_bfs = '\n'.join([f'\t\t\t\t{bf_id} /* {name} in Resources */,' for bf_id, fr_id, name, path, ftype in app_res_build_ids])
lines.append(f'''\t\t{app_resources_phase_id} /* Resources */ = {{
\t\t\tisa = PBXResourcesBuildPhase;
\t\t\tbuildActionMask = 2147483647;
\t\t\tfiles = (
{app_res_bfs}
\t\t\t);
\t\t\trunOnlyForDeploymentPostprocessing = 0;
\t\t}};''')

lines.append(f'''\t\t{test_resources_phase_id} /* Resources */ = {{
\t\t\tisa = PBXResourcesBuildPhase;
\t\t\tbuildActionMask = 2147483647;
\t\t\tfiles = (
\t\t\t);
\t\t\trunOnlyForDeploymentPostprocessing = 0;
\t\t}};''')
lines.append('/* End PBXResourcesBuildPhase section */')

# PBXSourcesBuildPhase
lines.append('\n/* Begin PBXSourcesBuildPhase section */')
app_src_bfs = '\n'.join([f'\t\t\t\t{bf_id} /* {name} in Sources */,' for bf_id, fr_id, name, path in app_src_build_ids])
lines.append(f'''\t\t{app_sources_phase_id} /* Sources */ = {{
\t\t\tisa = PBXSourcesBuildPhase;
\t\t\tbuildActionMask = 2147483647;
\t\t\tfiles = (
{app_src_bfs}
\t\t\t);
\t\t\trunOnlyForDeploymentPostprocessing = 0;
\t\t}};''')

test_src_bfs = '\n'.join([f'\t\t\t\t{bf_id} /* {name} in Sources */,' for bf_id, fr_id, name, path in test_src_build_ids])
lines.append(f'''\t\t{test_sources_phase_id} /* Sources */ = {{
\t\t\tisa = PBXSourcesBuildPhase;
\t\t\tbuildActionMask = 2147483647;
\t\t\tfiles = (
{test_src_bfs}
\t\t\t);
\t\t\trunOnlyForDeploymentPostprocessing = 0;
\t\t}};''')
lines.append('/* End PBXSourcesBuildPhase section */')

# PBXTargetDependency
lines.append('\n/* Begin PBXTargetDependency section */')
lines.append(f'''\t\t{target_dep_id} /* PBXTargetDependency */ = {{
\t\t\tisa = PBXTargetDependency;
\t\t\ttarget = {target_app_id} /* SantaList */;
\t\t\ttargetProxy = {container_proxy_id} /* PBXContainerItemProxy */;
\t\t}};''')
lines.append('/* End PBXTargetDependency section */')

# XCBuildConfiguration
lines.append('\n/* Begin XCBuildConfiguration section */')
lines.append(f'''\t\t{proj_debug_cfg_id} /* Debug */ = {{
\t\t\tisa = XCBuildConfiguration;
\t\t\tbuildSettings = {{
\t\t\t\tALWAYS_SEARCH_USER_PATHS = NO;
\t\t\t\tCLANG_ANALYZER_NONNULL = YES;
\t\t\t\tCLANG_ENABLE_MODULES = YES;
\t\t\t\tCOPY_PHASE_STRIP = NO;
\t\t\t\tDEBUG_INFORMATION_FORMAT = dwarf;
\t\t\t\tENABLE_STRICT_OBJC_MSGSEND = YES;
\t\t\t\tENABLE_TESTABILITY = YES;
\t\t\t\tGCC_DYNAMIC_NO_PIC = NO;
\t\t\t\tGCC_OPTIMIZATION_LEVEL = 0;
\t\t\t\tGCC_PREPROCESSOR_DEFINITIONS = (
\t\t\t\t\t"DEBUG=1",
\t\t\t\t\t"$(inherited)",
\t\t\t\t);
\t\t\t\tIPHONEOS_DEPLOYMENT_TARGET = 17.0;
\t\t\t\tMTL_ENABLE_DEBUG_INFO = INCLUDE_SOURCE;
\t\t\t\tONLY_ACTIVE_ARCH = YES;
\t\t\t\tSDKROOT = iphoneos;
\t\t\t\tSWIFT_ACTIVE_COMPILATION_CONDITIONS = "DEBUG $(inherited)";
\t\t\t\tSWIFT_OPTIMIZATION_LEVEL = "-Onone";
\t\t\t\tSWIFT_VERSION = 5.0;
\t\t\t}};
\t\t\tname = Debug;
\t\t}};''')

lines.append(f'''\t\t{proj_release_cfg_id} /* Release */ = {{
\t\t\tisa = XCBuildConfiguration;
\t\t\tbuildSettings = {{
\t\t\t\tALWAYS_SEARCH_USER_PATHS = NO;
\t\t\t\tCLANG_ANALYZER_NONNULL = YES;
\t\t\t\tCLANG_ENABLE_MODULES = YES;
\t\t\t\tCOPY_PHASE_STRIP = NO;
\t\t\t\tDEBUG_INFORMATION_FORMAT = "dwarf-with-dsym";
\t\t\t\tENABLE_NS_ASSERTIONS = NO;
\t\t\t\tENABLE_STRICT_OBJC_MSGSEND = YES;
\t\t\t\tGCC_OPTIMIZATION_LEVEL = s;
\t\t\t\tIPHONEOS_DEPLOYMENT_TARGET = 17.0;
\t\t\t\tMTL_ENABLE_DEBUG_INFO = NO;
\t\t\t\tSDKROOT = iphoneos;
\t\t\t\tSWIFT_COMPILATION_MODE = "wholemodule";
\t\t\t\tSWIFT_OPTIMIZATION_LEVEL = "-O";
\t\t\t\tSWIFT_VERSION = 5.0;
\t\t\t\tVALIDATE_PRODUCT = YES;
\t\t\t}};
\t\t\tname = Release;
\t\t}};''')

lines.append(f'''\t\t{app_debug_cfg_id} /* Debug */ = {{
\t\t\tisa = XCBuildConfiguration;
\t\t\tbuildSettings = {{
\t\t\t\tASSETCATALOG_COMPILER_APPICON_NAME = AppIcon;
\t\t\t\tASSETCATALOG_COMPILER_GLOBAL_ACCENT_COLOR_NAME = AccentColor;
\t\t\t\tCODE_SIGN_STYLE = Automatic;
\t\t\t\tCURRENT_PROJECT_VERSION = 1;
\t\t\t\tDEVELOPMENT_TEAM = "";
\t\t\t\tENABLE_PREVIEWS = YES;
\t\t\t\tGENERATE_INFOPLIST_FILE = NO;
\t\t\t\tINFOPLIST_FILE = SantaList/Info.plist;
\t\t\t\tINFOPLIST_KEY_CFBundleDisplayName = SantaList;
\t\t\t\tIPHONEOS_DEPLOYMENT_TARGET = 17.0;
\t\t\t\tLD_RUNPATH_SEARCH_PATHS = (
\t\t\t\t\t"$(inherited)",
\t\t\t\t\t"@executable_path/Frameworks",
\t\t\t\t);
\t\t\t\tMARKETING_VERSION = 1.0.0;
\t\t\t\tPRODUCT_BUNDLE_IDENTIFIER = com.santalist.app;
\t\t\t\tPRODUCT_NAME = "$(TARGET_NAME)";
\t\t\t\tSWIFT_EMIT_LOC_STRINGS = YES;
\t\t\t\tSWIFT_VERSION = 5.0;
\t\t\t\tTARGETED_DEVICE_FAMILY = "1,2";
\t\t\t}};
\t\t\tname = Debug;
\t\t}};''')

lines.append(f'''\t\t{app_release_cfg_id} /* Release */ = {{
\t\t\tisa = XCBuildConfiguration;
\t\t\tbuildSettings = {{
\t\t\t\tASSETCATALOG_COMPILER_APPICON_NAME = AppIcon;
\t\t\t\tASSETCATALOG_COMPILER_GLOBAL_ACCENT_COLOR_NAME = AccentColor;
\t\t\t\tCODE_SIGN_STYLE = Automatic;
\t\t\t\tCURRENT_PROJECT_VERSION = 1;
\t\t\t\tDEVELOPMENT_TEAM = "";
\t\t\t\tENABLE_PREVIEWS = YES;
\t\t\t\tGENERATE_INFOPLIST_FILE = NO;
\t\t\t\tINFOPLIST_FILE = SantaList/Info.plist;
\t\t\t\tINFOPLIST_KEY_CFBundleDisplayName = SantaList;
\t\t\t\tIPHONEOS_DEPLOYMENT_TARGET = 17.0;
\t\t\t\tLD_RUNPATH_SEARCH_PATHS = (
\t\t\t\t\t"$(inherited)",
\t\t\t\t\t"@executable_path/Frameworks",
\t\t\t\t);
\t\t\t\tMARKETING_VERSION = 1.0.0;
\t\t\t\tPRODUCT_BUNDLE_IDENTIFIER = com.santalist.app;
\t\t\t\tPRODUCT_NAME = "$(TARGET_NAME)";
\t\t\t\tSWIFT_EMIT_LOC_STRINGS = YES;
\t\t\t\tSWIFT_VERSION = 5.0;
\t\t\t\tTARGETED_DEVICE_FAMILY = "1,2";
\t\t\t}};
\t\t\tname = Release;
\t\t}};''')

lines.append(f'''\t\t{test_debug_cfg_id} /* Debug */ = {{
\t\t\tisa = XCBuildConfiguration;
\t\t\tbuildSettings = {{
\t\t\t\tBUNDLE_LOADER = "$(TEST_HOST)";
\t\t\t\tCODE_SIGN_STYLE = Automatic;
\t\t\t\tCURRENT_PROJECT_VERSION = 1;
\t\t\t\tDEVELOPMENT_TEAM = "";
\t\t\t\tGENERATE_INFOPLIST_FILE = YES;
\t\t\t\tIPHONEOS_DEPLOYMENT_TARGET = 17.0;
\t\t\t\tLD_RUNPATH_SEARCH_PATHS = (
\t\t\t\t\t"$(inherited)",
\t\t\t\t\t"@executable_path/Frameworks",
\t\t\t\t\t"@loader_path/Frameworks",
\t\t\t\t);
\t\t\t\tMARKETING_VERSION = 1.0.0;
\t\t\t\tPRODUCT_BUNDLE_IDENTIFIER = com.santalist.appTests;
\t\t\t\tPRODUCT_NAME = "$(TARGET_NAME)";
\t\t\t\tSWIFT_VERSION = 5.0;
\t\t\t\tTARGETED_DEVICE_FAMILY = "1,2";
\t\t\t\tTEST_HOST = "$(BUILT_PRODUCTS_DIR)/SantaList.app/$(BUNDLE_EXECUTABLE_FOLDER_PATH)/SantaList";
\t\t\t}};
\t\t\tname = Debug;
\t\t}};''')

lines.append(f'''\t\t{test_release_cfg_id} /* Release */ = {{
\t\t\tisa = XCBuildConfiguration;
\t\t\tbuildSettings = {{
\t\t\t\tBUNDLE_LOADER = "$(TEST_HOST)";
\t\t\t\tCODE_SIGN_STYLE = Automatic;
\t\t\t\tCURRENT_PROJECT_VERSION = 1;
\t\t\t\tDEVELOPMENT_TEAM = "";
\t\t\t\tGENERATE_INFOPLIST_FILE = YES;
\t\t\t\tIPHONEOS_DEPLOYMENT_TARGET = 17.0;
\t\t\t\tLD_RUNPATH_SEARCH_PATHS = (
\t\t\t\t\t"$(inherited)",
\t\t\t\t\t"@executable_path/Frameworks",
\t\t\t\t\t"@loader_path/Frameworks",
\t\t\t\t);
\t\t\t\tMARKETING_VERSION = 1.0.0;
\t\t\t\tPRODUCT_BUNDLE_IDENTIFIER = com.santalist.appTests;
\t\t\t\tPRODUCT_NAME = "$(TARGET_NAME)";
\t\t\t\tSWIFT_VERSION = 5.0;
\t\t\t\tTARGETED_DEVICE_FAMILY = "1,2";
\t\t\t\tTEST_HOST = "$(BUILT_PRODUCTS_DIR)/SantaList.app/$(BUNDLE_EXECUTABLE_FOLDER_PATH)/SantaList";
\t\t\t}};
\t\t\tname = Release;
\t\t}};''')
lines.append('/* End XCBuildConfiguration section */')

# XCConfigurationList
lines.append('\n/* Begin XCConfigurationList section */')
lines.append(f'''\t\t{proj_cfg_list_id} /* Build configuration list for PBXProject "SantaList" */ = {{
\t\t\tisa = XCConfigurationList;
\t\t\tbuildConfigurations = (
\t\t\t\t{proj_debug_cfg_id} /* Debug */,
\t\t\t\t{proj_release_cfg_id} /* Release */,
\t\t\t);
\t\t\tdefaultConfigurationIsVisible = 0;
\t\t\tdefaultConfigurationName = Release;
\t\t}};''')

lines.append(f'''\t\t{app_cfg_list_id} /* Build configuration list for PBXNativeTarget "SantaList" */ = {{
\t\t\tisa = XCConfigurationList;
\t\t\tbuildConfigurations = (
\t\t\t\t{app_debug_cfg_id} /* Debug */,
\t\t\t\t{app_release_cfg_id} /* Release */,
\t\t\t);
\t\t\tdefaultConfigurationIsVisible = 0;
\t\t\tdefaultConfigurationName = Release;
\t\t}};''')

lines.append(f'''\t\t{test_cfg_list_id} /* Build configuration list for PBXNativeTarget "SantaListTests" */ = {{
\t\t\tisa = XCConfigurationList;
\t\t\tbuildConfigurations = (
\t\t\t\t{test_debug_cfg_id} /* Debug */,
\t\t\t\t{test_release_cfg_id} /* Release */,
\t\t\t);
\t\t\tdefaultConfigurationIsVisible = 0;
\t\t\tdefaultConfigurationName = Release;
\t\t}};''')
lines.append('/* End XCConfigurationList section */')

lines.append('\t};')
lines.append(f'\trootObject = {project_id} /* Project object */;')
lines.append('}')

with open(os.path.join(project_dir, 'project.pbxproj'), 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines) + '\n')

print('SantaList.xcodeproj/project.pbxproj generated successfully!')
